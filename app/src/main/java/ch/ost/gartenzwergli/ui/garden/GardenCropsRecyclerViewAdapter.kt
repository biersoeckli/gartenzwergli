package ch.ost.gartenzwergli.ui.garden

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import ch.ost.gartenzwergli.R
import ch.ost.gartenzwergli.databinding.GardenCropItemBinding
import ch.ost.gartenzwergli.model.dbo.cropevent.CropEventAndCrop
import ch.ost.gartenzwergli.ui.crops.cropDetail.CropDetailActivity
import ch.ost.gartenzwergli.ui.crops.titlecase
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GardenCropsRecyclerViewAdapter(
    private val values: List<CropEventAndCrop>,
    private val context: Context
): RecyclerView.Adapter<GardenCropsRecyclerViewAdapter.ViewHolder>() {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            GardenCropItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        var isDummyCrop = false

        val titleDate = " - ${LocalDate.parse(item.cropEvent.dateTime).format(dateFormatter)}"

        holder.cropNameTextView.text = item.crop?.name?.titlecase() + titleDate
        if (item.crop?.id == "0") {
            isDummyCrop = true
            holder.cropNameTextView.text = item.cropEvent.title + titleDate
        }

        if (item.cropEvent.description?.isEmpty() == true) {
            holder.cropDescriptionTextView.isVisible = false
        }
        
        holder.cropDescriptionTextView.text = item.cropEvent.description
        holder.cropPlantedOnTextView.text = context.getString(
            R.string.planted_on,
            LocalDate.parse(item.cropEvent.plantedTime).format(dateFormatter)
        )

        if (item.crop?.thumnailPath != null) {
            val imgFile = File(item.crop?.thumnailPath)
            if (imgFile.exists()) {
                val cropBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                holder.cropImageView.setImageBitmap(cropBitmap)
            }
        }

        holder.cropLearnMoreButton.setOnClickListener {
            val intent = Intent(context, CropDetailActivity::class.java)
            intent.putExtra(CropDetailActivity.EXTRA_CROP_DBO_ID, item.crop?.id)
            context.startActivity(intent)
        }

        if (isDummyCrop) {
            holder.cropLearnMoreButton.isVisible = false
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: GardenCropItemBinding): RecyclerView.ViewHolder(binding.root) {
        val cropImageView = binding.cropImageView
        val cropNameTextView = binding.cropNameTextView
        val cropDescriptionTextView = binding.cropDescriptionTextView
        val cropPlantedOnTextView = binding.cropPlantedOnTextView
        val cropLearnMoreButton = binding.cropLearnMoreButton

        override fun toString(): String {
            return super.toString() + " '" + cropNameTextView.text + "'"
        }
    }
}