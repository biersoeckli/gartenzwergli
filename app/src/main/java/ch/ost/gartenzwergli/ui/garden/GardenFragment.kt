package ch.ost.gartenzwergli.ui.garden

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ch.ost.gartenzwergli.GardenCameraActivity
import ch.ost.gartenzwergli.R
import ch.ost.gartenzwergli.databinding.FragmentGardenBinding

class GardenFragment : Fragment() {
    private var _binding: FragmentGardenBinding? = null

    private val binding get() = _binding!!

    companion object {
        private const val TAG = "GardenFragment"
    }

    private lateinit var viewModel: GardenViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGardenBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.gardenToolbar.inflateMenu(R.menu.menu_garden)

        binding.gardenToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.garden_settings -> {
                    val cameraActivity = Intent(this.context, GardenCameraActivity::class.java)
                    startActivity(cameraActivity)

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