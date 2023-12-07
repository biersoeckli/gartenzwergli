package ch.ost.gartenzwergli.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import cancelNotificationForCropEvent
import ch.ost.gartenzwergli.GartenzwergliApplication
import ch.ost.gartenzwergli.R
import ch.ost.gartenzwergli.databinding.CalendarDayBinding
import ch.ost.gartenzwergli.databinding.FragmentHomeBinding
import ch.ost.gartenzwergli.model.dbo.DUMMY_CROP_ID
import ch.ost.gartenzwergli.model.dbo.cropevent.CropEventAndCrop
import ch.ost.gartenzwergli.model.dbo.cropevent.CropEventHarvestItem
import ch.ost.gartenzwergli.services.DatabaseService
import ch.ost.gartenzwergli.ui.garden.GardenViewModel
import ch.ost.gartenzwergli.ui.garden.GardenViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.coroutines.CoroutineContext

class HomeFragment() : Fragment(), CoroutineScope {

    private var _cropsItems: List<CropEventAndCrop>? = null
    private var cropsItems: List<CropEventAndCrop>?
        get() = _cropsItems
        set(value) {
            _cropsItems = value
        }

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    private var cropEventsAdapter: CropEventsRecyclerViewAdapter? = null

    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()

    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")

    private val gardenViewModel: GardenViewModel by viewModels {
        GardenViewModelFactory((requireActivity().application as GartenzwergliApplication).cropEventRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cropEventsAdapter = CropEventsRecyclerViewAdapter(mutableListOf()) // ititialize empty list
    }
    fun getEventItemsByDay(date: LocalDate, harvestType: String = "ANY_TYPE_EVENT"): List<CropEventAndCrop> {
        val harvestItemsFilteredByDate = cropsItems?.filter { it.cropEvent.dateTime == date.format(DateTimeFormatter.ISO_DATE) }

        var harvestItems = harvestItemsFilteredByDate?.filter { it.cropEvent.description == harvestType }
        if (harvestType == "ANY_TYPE_EVENT") {
            harvestItems = harvestItemsFilteredByDate?.filter {
                it.cropEvent.description != "Sewed" && it.cropEvent.description != "First Harvest" && it.cropEvent.description != "Last Harvest"
            }
        }
        return harvestItems ?: listOf()
    }

    fun refreshCropsOnUi(date: LocalDate? = null) {
        gardenViewModel.cropEventAndCrops.observe(viewLifecycleOwner) { cropEvents ->
            cropsItems = cropEvents
            val filteredCropEvents = cropEvents.filter { it.cropEvent.dateTime == date?.format(DateTimeFormatter.ISO_DATE) }

            val recyclerView: RecyclerView = binding.calendarEventsRecyclerView
            recyclerView.adapter = CropEventsRecyclerViewAdapter(filteredCropEvents)
            updateEmptyView(recyclerView, binding.root)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.homeToolbar.inflateMenu(R.menu.menu_home)
        binding.homeToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.today_menu_item -> {
                    selectDate(today)
                    true
                }
                else -> {
                    false
                }
            }
        }

        val floatingActionButtonNewCropEvent: FloatingActionButton =
            binding.floatingActionButtonNewCropEvent
        floatingActionButtonNewCropEvent.setOnClickListener {
            if (selectedDate == null) {
                selectedDate = today
            }
            val dialogFragment = selectedDate?.format(DateTimeFormatter.ISO_DATE)
                ?.let { it1 -> NewEventDialog.newInstance(it1) }

            dialogFragment?.onSuccessfullySavedAction?.observe(viewLifecycleOwner) {
                //loadCropsItems()
                //refreshCropsOnUi()
            }
            dialogFragment?.show(
                childFragmentManager, null
            )
        }

        val recyclerViewCropEvents: RecyclerView = binding.calendarEventsRecyclerView
        recyclerViewCropEvents.adapter = cropEventsAdapter

        val swipeController = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.absoluteAdapterPosition
                val cropEvent: CropEventAndCrop? = cropEventsAdapter?.values?.get(position);

                if (cropEvent != null) {
                    DatabaseService.getDb().cropEventDao().delete(cropEvent.cropEvent)
                    if (cropEvent.cropEvent.id != DUMMY_CROP_ID) {
                        cancelNotificationForCropEvent(context!!, cropEvent.cropEvent.id)
                    }
                }

                cropEventsAdapter?.values?.drop(position)
                cropEventsAdapter?.notifyItemRemoved(position)

                val snackbar = Snackbar.make(
                    recyclerViewCropEvents,
                    "Item was removed from the list.",
                    Snackbar.LENGTH_LONG
                )

                updateEmptyView(recyclerViewCropEvents, binding.root)

                // nice to have feature
                /*snackbar.setAction("UNDO") {
                    if (cropEvent != null) {
                        cropEventsAdapter?.values?.add(position, cropEvent)
                    }
                    cropEventsAdapter?.notifyItemInserted(position)
                    recyclerViewCropEvents.scrollToPosition(position)
                }*/
                snackbar.setActionTextColor(Color.YELLOW)
                snackbar.anchorView = floatingActionButtonNewCropEvent
                snackbar.show()
            }
        }

        val selectedDateText: TextView = binding.selectedDateText
        selectedDateText.text = selectionFormatter.format(today)

        // recycler view swipe right to show delete button
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(recyclerViewCropEvents)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val daysOfWeek = daysOfWeek()

        binding.legendLayout.root.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                textView.text =
                    daysOfWeek[index].getDisplayName(TextStyle.SHORT, Locale.getDefault())
                textView.setTextColor(Color.WHITE)
            }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(200)
        val endMonth = currentMonth.plusMonths(200)

        gardenViewModel.cropEventAndCrops.observe(viewLifecycleOwner) { cropEvents ->
            cropsItems = cropEvents
            val filteredCropEvents = cropEvents.filter { it.cropEvent.dateTime == today?.format(DateTimeFormatter.ISO_DATE) }

            setupMonthCalendar(startMonth, endMonth, currentMonth, daysOfWeek.first())

            val recyclerView: RecyclerView = binding.calendarEventsRecyclerView
            recyclerView.adapter = CropEventsRecyclerViewAdapter(filteredCropEvents)
            updateEmptyView(recyclerView, binding.root)
        }
    }

    private fun updateEmptyView(recyclerView: RecyclerView, view: View) {
        val emptyView: TextView = view.findViewById(R.id.emptyEventsViewText)
        if (recyclerView.adapter?.itemCount == 0) {
            emptyView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    private fun setupMonthCalendar(
        startMonth: YearMonth,
        endMonth: YearMonth,
        currentMonth: YearMonth,
        firstDayOfWeek: DayOfWeek
    ) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView = CalendarDayBinding.bind(view).dayText

            val dotView = CalendarDayBinding.bind(view).dayDotViewSewedCrop
            val dotViewFirstHarvest = CalendarDayBinding.bind(view).dayDotViewFirstHarvest
            val dotViewLastHarvest = CalendarDayBinding.bind(view).dayDotViewLastHarvest
            val dotViewCustomEvent = CalendarDayBinding.bind(view).dayDotViewCustomEvent

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        selectDate(day.date)
                    }
                }
            }
        }

        binding.calendarViewCropCalendar.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val textView = container.textView
                val dotViewCrop = container.dotView
                val dotViewFirstHarvest = container.dotViewFirstHarvest
                val dotViewLastHarvest = container.dotViewLastHarvest
                val dotViewCustomEvent = container.dotViewCustomEvent

                textView.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    textView.visibility = View.VISIBLE

                    when (data.date) {
                        today -> {
                            textView.setTextColor(Color.WHITE)
                            textView.setBackgroundResource(androidx.appcompat.R.color.material_deep_teal_200)
                        }

                        selectedDate -> {
                            textView.setTextColor(Color.BLUE)
                            textView.setBackgroundColor(Color.LTGRAY)
                        }

                        else -> {
                            textView.setTextColor(Color.BLACK)
                            textView.background = null
                        }
                    }
                } else {
                    textView.setTextColor(Color.GRAY)
                    dotViewCrop.visibility = View.INVISIBLE
                }

                dotViewCrop.isVisible = getEventItemsByDay(data.date, "Sewed").isNotEmpty()

                dotViewFirstHarvest.background.setTint(ContextCompat.getColor(context!!, R.color.calendar_icon_dark_green))
                dotViewFirstHarvest.isVisible = getEventItemsByDay(data.date, "First Harvest").isNotEmpty()

                dotViewLastHarvest.background.setTint(ContextCompat.getColor(context!!, androidx.appcompat.R.color.error_color_material_dark))
                dotViewLastHarvest.isVisible = getEventItemsByDay(data.date, "Last Harvest").isNotEmpty()

                dotViewCustomEvent.isVisible = getEventItemsByDay(data.date).isNotEmpty()

                if (dotViewCrop.isVisible && dotViewFirstHarvest.isVisible) {
                    setMargins(dotViewFirstHarvest, 0, 0, 10, 10)
                    setMargins(dotViewCrop, 10, 0, 0, 10)
                } else if (dotViewCrop.isVisible && dotViewFirstHarvest.isVisible && dotViewLastHarvest.isVisible) {
                    setMargins(dotViewFirstHarvest, 0, 0, 10, 10)
                    setMargins(dotViewCrop, 10, 0, 0, 10)
                    setMargins(dotViewLastHarvest, 10, 0, 0, 10)
                }

                if (dotViewCrop.isVisible || dotViewFirstHarvest.isVisible || dotViewLastHarvest.isVisible) {
                    dotViewCustomEvent.isVisible = false
                }
            }
        }

        binding.calendarViewCropCalendar.monthScrollListener = {
            updateTitle()
        }
        binding.calendarViewCropCalendar.setup(startMonth, endMonth, firstDayOfWeek)
        binding.calendarViewCropCalendar.scrollToMonth(currentMonth)
    }

    fun setMargins(v: View, l: Int, t: Int, r: Int, b: Int) {
        if (v.layoutParams is MarginLayoutParams) {
            val p = v.layoutParams as MarginLayoutParams
            p.setMargins(l, t, r, b)
            v.requestLayout()
        }
    }

    private fun updateTitle() {
        val month = binding.calendarViewCropCalendar.findFirstVisibleMonth()?.yearMonth ?: return
        binding.exOneYearText.text = month.year.toString()
        binding.exOneMonthText.text =
            month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date

            oldDate?.let { binding.calendarViewCropCalendar.notifyDateChanged(it) }
            binding.calendarViewCropCalendar.notifyDateChanged(date)
            binding.calendarViewCropCalendar.scrollToDate(date)
            refreshCropsOnUi(date)
            binding.selectedDateText.text = selectionFormatter.format(date)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}