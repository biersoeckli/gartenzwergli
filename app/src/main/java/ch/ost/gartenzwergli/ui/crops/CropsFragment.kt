package ch.ost.gartenzwergli.ui.crops

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import ch.ost.gartenzwergli.R
import ch.ost.gartenzwergli.databinding.FragmentCropsBinding
import ch.ost.gartenzwergli.services.DatabaseService
import com.google.android.material.internal.TextWatcherAdapter
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * A fragment representing a list of Items.
 */
class CropsFragment : Fragment(), CoroutineScope {

    private var columnCount = 1
    private var searchBarText = "Search Crops"

    private var _binding: FragmentCropsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
            searchBarText = it.getString(SEARCH_BAR_TEXT).toString()
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCropsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val cropsRecyclerView: RecyclerView = binding.cropsListRecyclerView
        val baseCropsRecyclerView: RecyclerView = binding.baseCropsListRecyclerView

        val cropsSearchBar: SearchBar = binding.cropsSearchBar
        val cropsSearchView: SearchView = binding.searchCropsView

        cropsSearchView.setupWithSearchBar(cropsSearchBar)

        cropsSearchBar.hint = searchBarText

        with(baseCropsRecyclerView) {
            layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(context)
                else -> GridLayoutManager(context, columnCount)
            }

            launch {
                val cropDbos = DatabaseService.getDb().cropDao().getAll()
                adapter = CropsRecyclerViewAdapter(cropDbos, baseCropsRecyclerView.context)
            }
        }

        cropsSearchView.editText.addTextChangedListener(object : TextWatcherAdapter() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val searchQuery = cropsSearchView.text.toString()

                launch {
                    val cropDbos = DatabaseService.getDb().cropDao().findByName("%$searchQuery%")

                    // change data in adapter
                    cropsRecyclerView.adapter = CropsRecyclerViewAdapter(cropDbos, cropsRecyclerView.context)
                }
            }
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAppBarMenu()
    }

    private fun setupAppBarMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.crops_app_bar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"
        const val SEARCH_BAR_TEXT = "Search Crops"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(searchBarText: String, columnCount: Int) =
            CropsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                    putString(SEARCH_BAR_TEXT, searchBarText)
                }
            }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}