package ch.ost.gartenzwergli.ui.garden

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ch.ost.gartenzwergli.R
import ch.ost.gartenzwergli.databinding.FragmentGardenBinding
import ch.ost.gartenzwergli.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar

class GardenFragment : Fragment() {

    private var _binding: FragmentGardenBinding? = null

    private val binding get() = _binding!!

    companion object {
        fun newInstance() = GardenFragment()
    }

    private lateinit var viewModel: GardenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGardenBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.gardenToolbar.inflateMenu(R.menu.menu_garden)

        binding.gardenToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.garden_settings -> {
                    // Handle favorite icon press
                    Snackbar.make(root, "Settings", Snackbar.LENGTH_SHORT).show()
                    true
                }

                else -> {
                    false
                }
            }
        }

        binding.addPlantFab.setOnClickListener { view ->

        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(GardenViewModel::class.java)
        // TODO: Use the ViewModel
    }

}