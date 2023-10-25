package ch.ost.gartenzwergli.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ch.ost.gartenzwergli.R
import ch.ost.gartenzwergli.databinding.CalendarDayBinding
import ch.ost.gartenzwergli.databinding.FragmentHomeBinding
import ch.ost.gartenzwergli.model.dbo.cropevent.CropEventAndCrop
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.coroutines.CoroutineContext

class HomeFragment() : Fragment(), CoroutineScope {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var cropEventsAdapter: CropEventsRecyclerViewAdapter? = null

    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()

    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cropEvents = mutableListOf<CropEventAndCrop>(
            CropEventAndCrop(
                cropEvent = ch.ost.gartenzwergli.model.dbo.cropevent.CropEventDbo(
                    title = "Crop Event 1",
                    dateTime = "2021-05-01T12:00:00.000+00:00",
                    cropId = "1",
                    id = "1",
                    description = "Description 1"
                ),
                crop = null
            ),
        )
        cropEventsAdapter = CropEventsRecyclerViewAdapter(cropEvents);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        setupAppBarMenu()

        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val floatingActionButtonNewCropEvent: FloatingActionButton =
            binding.floatingActionButtonNewCropEvent
        floatingActionButtonNewCropEvent.setOnClickListener {
            NewEventDialog().show(
                childFragmentManager, null
            )
        }

        val recyclerViewCropEvents: RecyclerView = binding.calendarEventsRecyclerView
        recyclerViewCropEvents.adapter = cropEventsAdapter

        val swipeController = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.absoluteAdapterPosition
                val cropEvent: CropEventAndCrop? = cropEventsAdapter?.values?.get(position);

                cropEventsAdapter?.values?.removeAt(position)
                cropEventsAdapter?.notifyItemRemoved(position)

                val snackbar = Snackbar.make(
                    recyclerViewCropEvents,
                    "Item was removed from the list.",
                    Snackbar.LENGTH_LONG
                )
                snackbar.setAction("UNDO") {
                    if (cropEvent != null) {
                        cropEventsAdapter?.values?.add(position, cropEvent)
                    }
                    cropEventsAdapter?.notifyItemInserted(position)
                    recyclerViewCropEvents.scrollToPosition(position)
                }
                snackbar.setActionTextColor(Color.YELLOW)
                snackbar.anchorView = floatingActionButtonNewCropEvent
                snackbar.show()
            }
        }

        // recycler view swipe right to show delete button
        val itemTouchHelper = ItemTouchHelper(swipeController)
        itemTouchHelper.attachToRecyclerView(recyclerViewCropEvents)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.calendarViewCropCalendar.monthScrollListener = {
            selectDate(it.yearMonth.atDay(1))
        }

        val daysOfWeek = daysOfWeek()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(200)
        val endMonth = currentMonth.plusMonths(200)

        configureBinders(daysOfWeek)

        binding.calendarViewCropCalendar.apply {
            setup(startMonth, endMonth, daysOfWeek.first())
            scrollToMonth(currentMonth)
        }
    }

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date

            oldDate?.let { binding.calendarViewCropCalendar.notifyDateChanged(it) }
            binding.calendarViewCropCalendar.notifyDateChanged(date)
            updateAdapterForDate(date)
        }
    }

    private fun updateAdapterForDate(date: LocalDate) {
        val cropEvents = mutableListOf<CropEventAndCrop>(
            CropEventAndCrop(
                cropEvent = ch.ost.gartenzwergli.model.dbo.cropevent.CropEventDbo(
                    title = "Crop Event 1",
                    dateTime = "2021-05-01T12:00:00.000+00:00",
                    cropId = "1",
                    id = "1",
                    description = "Description 1"
                ),
                crop = null
            ),
        )
        cropEventsAdapter.apply {
            this?.values?.clear()
            this?.values = cropEvents
            this?.notifyDataSetChanged()
        }
        binding.selectedDateText.text = selectionFormatter.format(date)
    }

    private fun configureBinders(daysOfWeek: List<DayOfWeek>) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val binding = CalendarDayBinding.bind(view)

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
                val textView = container.binding.dayText
                val dotView = container.binding.dayDotView

                textView.text = data.date.dayOfMonth.toString()

                if (data.position == DayPosition.MonthDate) {
                    textView.visibility = View.VISIBLE
                    when (data.date) {
                        today -> {
                            textView.setTextColor(Color.WHITE)
                            textView.setBackgroundResource(R.drawable.ic_today_white_24dp)
                            dotView.visibility = View.INVISIBLE
                        }

                        selectedDate -> {
                            textView.setTextColor(Color.BLUE)
                            textView.setBackgroundColor(Color.LTGRAY)
                            dotView.visibility = View.INVISIBLE
                        }

                        else -> {
                            textView.setTextColor(Color.BLACK)
                            textView.background = null

                            // set visible if data is available for the day
                            dotView.isVisible = true
                        }
                    }
                } else {
                    textView.visibility = View.INVISIBLE
                    dotView.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun setupAppBarMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.home_app_bar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.app_bar_today -> {
                        Toast.makeText(context, "Set Calendar to today", Toast.LENGTH_SHORT).show()
                        return true
                    }
                }
                return true
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}