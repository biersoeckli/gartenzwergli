package ch.ost.gartenzwergli.ui.home

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ch.ost.gartenzwergli.R
import ch.ost.gartenzwergli.databinding.CropEventItemBinding

class CropEventsRecyclerViewAdapter(
    private val values: List<CropEvent>
) : RecyclerView.Adapter<CropEventsRecyclerViewAdapter.ViewHolder>() {

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
        holder.textViewEvent.text = item.title
        holder.textViewCropName.text = item.cropName
        holder.textViewEventTime.text = item.time

        holder.imageViewEvent.setImageResource(R.drawable.ic_home_black_24dp)
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