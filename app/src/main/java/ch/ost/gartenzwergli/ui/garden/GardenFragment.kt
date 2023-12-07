package ch.ost.gartenzwergli.ui.garden

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import ch.ost.gartenzwergli.GardenCameraActivity
import ch.ost.gartenzwergli.GartenzwergliApplication
import ch.ost.gartenzwergli.R
import ch.ost.gartenzwergli.databinding.FragmentGardenBinding
import ch.ost.gartenzwergli.model.dbo.cropevent.CropEventAndCrop
import ch.ost.gartenzwergli.ui.crops.SelectCropActivity
import ch.ost.gartenzwergli.utils.BitmapUtils

class GardenFragment : Fragment() {
    private var _binding: FragmentGardenBinding? = null

    private val binding get() = _binding!!

    private val gardenViewModel: GardenViewModel by viewModels {
        GardenViewModelFactory((requireActivity().application as GartenzwergliApplication).cropEventRepository)
    }

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

        val sharedPref = activity?.applicationContext?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        sharedPref?.all?.forEach { (key, value) ->
            Log.d(TAG, "key: $key, value: $value")
        }

        val gardenBackgroundImage = sharedPref?.getString(getString(R.string.garden_background_image_key), null)

        if (gardenBackgroundImage != null) {
            val gardenBackgroundImageUri = Uri.parse(gardenBackgroundImage)
            try {
                val bitmap = this.activity?.let { BitmapUtils().getCapturedImage(it.contentResolver, gardenBackgroundImageUri) }
                binding.gardenBackgroundImage.setImageBitmap(bitmap)
            } catch (e: Exception) {
                Log.e(TAG, "Error while loading garden background image: ${e.message}")
                // unset background image
                with (sharedPref.edit()) {
                    putString(getString(R.string.garden_background_image_key), null)
                    apply()
                }
            }
        }

        binding.addPlantFab.setOnClickListener { view ->
            val cropSelectionActivity = Intent(this.context, SelectCropActivity::class.java)
            cropSelectionActivity.putExtra("searchBarText", getString(R.string.search_box_text_selection))
            startActivity(cropSelectionActivity)
        }

        return root
    }

    private fun refreshCropsOnUi() {
        val crops: MutableList<CropEventAndCrop>?;


    }

    private fun updateEmptyView(recyclerView: RecyclerView, view: View) {
        val emptyView: TextView = view.findViewById(R.id.emptyGardenTextView)
        if (recyclerView.adapter?.itemCount == 0) {
            view.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            view.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gardenViewModel.cropEventAndCrops.observe(viewLifecycleOwner, { cropEventAndCrops ->
            val recyclerView: RecyclerView = binding.gardenListRecyclerView
            recyclerView.adapter = GardenCropsRecyclerViewAdapter(cropEventAndCrops, view.context)
            updateEmptyView(recyclerView, binding.emptyGardenTextView)
        })
    }

    companion object {
        private const val TAG = "GardenFragment"
    }
}