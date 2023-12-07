package ch.ost.gartenzwergli.ui.home

import android.graphics.BitmapFactory
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ch.ost.gartenzwergli.databinding.CropEventItemBinding
import ch.ost.gartenzwergli.model.dbo.cropevent.CropEventAndCrop
import ch.ost.gartenzwergli.ui.crops.titlecase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CropEventsRecyclerViewAdapter(
    var values: List<CropEventAndCrop>
) : RecyclerView.Adapter<CropEventsRecyclerViewAdapter.ViewHolder>() {

    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CropEventItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.textViewEvent.text = item.cropEvent.title.titlecase()
        holder.textViewCropName.text = item.cropEvent?.description ?: ""
        holder.textViewEventTime.text = "Planted: ${LocalDate.parse(item.cropEvent.plantedTime).format(formatter)}"

        if (item.crop?.thumnailPath != null) {
            val imgFile = item.crop.thumnailPath
            if (imgFile != null) {
                val bmp = BitmapFactory.decodeFile(imgFile);
                holder.imageViewEvent.setImageBitmap(bmp)
            }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: CropEventItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val textViewEvent: TextView = binding.textViewEvent
        val textViewCropName: TextView = binding.textViewCropName
        val textViewEventTime: TextView = binding.textViewEventTime
        val imageViewEvent: ImageView = binding.imageViewEvent

        override fun toString(): String {
            return "ViewHolder(textViewEvent=$textViewEvent, textViewCropName=$textViewCropName, textViewEventTime=$textViewEventTime)"
        }
    }

}