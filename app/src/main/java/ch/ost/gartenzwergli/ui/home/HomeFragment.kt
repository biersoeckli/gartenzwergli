package ch.ost.gartenzwergli.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import ch.ost.gartenzwergli.R
import ch.ost.gartenzwergli.databinding.FragmentHomeBinding
import ch.ost.gartenzwergli.ui.crops.CropsRecyclerViewAdapter
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val floatingActionButtonNewCropEvent: FloatingActionButton = binding.floatingActionButtonNewCropEvent
        floatingActionButtonNewCropEvent.setOnClickListener {
            NewEventDialog().show(
                childFragmentManager, null
            )
        }

        val cropEvents = listOf(
            CropEvent("Test", "Tomaten", "11:30"),
            CropEvent("Dies ist ein kleiner Test", "Orangen", "")
        )
        val cropEventsAdapter = CropEventsRecyclerViewAdapter(cropEvents);

        val recyclerViewCropEvents: RecyclerView = binding.calendarEventsRecyclerView
        recyclerViewCropEvents.adapter = cropEventsAdapter;

        val event: EventDay = EventDay(Calendar.getInstance(), R.drawable.ic_home_black_24dp)
        val cropCalendar: CalendarView = binding.calendarViewCropCalendar
        cropCalendar.setEvents(listOf(event));

        setupAppBarMenu()

        return root
    }

    private fun setupAppBarMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.home_app_bar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.app_bar_today -> {
                        binding.calendarViewCropCalendar.setDate(Calendar.getInstance());

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
}