package ch.ost.gartenzwergli.ui.crops

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ch.ost.gartenzwergli.R

import ch.ost.gartenzwergli.ui.crops.placeholder.PlaceholderContent.PlaceholderItem
import ch.ost.gartenzwergli.databinding.CropItemBinding
import ch.ost.gartenzwergli.model.CropDto

class CropsRecyclerViewAdapter(
    private val values: List<CropDto>
) : RecyclerView.Adapter<CropsRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            CropItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.idView.text = item.id
        holder.contentView.text = item.name
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: CropItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}