package ch.ost.gartenzwergli.ui.crops

import android.os.Bundle
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
import ch.ost.gartenzwergli.model.GrowstuffCropDto
import ch.ost.gartenzwergli.services.RestClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A fragment representing a list of Items.
 */
class CropsFragment : Fragment() {

    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.crop_item_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }


                val apiService = RestClient.getGrowstuffClient()
                val call: Call<List<GrowstuffCropDto>> = apiService.getCrops()
                call.enqueue(object : Callback<List<GrowstuffCropDto>> {
                    override fun onResponse(
                        call: Call<List<GrowstuffCropDto>>,
                        response: Response<List<GrowstuffCropDto>>
                    ) {
                        if (response.isSuccessful) {
                            val cropsList = response.body()
                            if (cropsList != null)
                                adapter = CropsRecyclerViewAdapter(cropsList)
                        } else {
                            // Todo Fehlerbehandlung
                        }
                    }

                    override fun onFailure(call: Call<List<GrowstuffCropDto>>, t: Throwable) {
                        // Todo Fehlerbehandlung
                    }
                })
            }
        }
        return view
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

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            CropsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}