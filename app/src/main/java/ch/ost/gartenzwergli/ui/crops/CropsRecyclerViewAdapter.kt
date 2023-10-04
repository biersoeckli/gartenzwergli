package ch.ost.gartenzwergli.ui.crops

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.ost.gartenzwergli.databinding.CropItemBinding
import ch.ost.gartenzwergli.model.GrowstuffCropDto
import java.net.URL


class CropsRecyclerViewAdapter(
    private val values: List<GrowstuffCropDto>
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
        // load thumbnail from url

        if (item.thumbnail_url == null) {
            return
        }
        // todo find other solution to load iamge
        /*
        val runnable = Runnable {
            val url = URL(item.thumbnail_url)
            val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            holder.thumbnailImageView.setImageBitmap(bmp)
        }
        val thread: Thread = Thread(runnable)
        thread.start()*/

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: CropItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val thumbnailImageView: ImageView = binding.thumbnailmageView
        val contentView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}