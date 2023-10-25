package ch.ost.gartenzwergli.ui.crops

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.ost.gartenzwergli.ui.cropDetail.CropDetailActivity
import ch.ost.gartenzwergli.databinding.CropItemBinding
import ch.ost.gartenzwergli.model.dbo.CropDbo
import java.io.File


class CropsRecyclerViewAdapter(
    private val values: List<CropDbo>,
    private val mContext: Context
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
        holder.plantNameTextView.text = item.name

        if (item.thumnailPath != null) {
            val imgFile = File(item.thumnailPath!!);
            if (imgFile.exists()) {
                val bmp = BitmapFactory.decodeFile(imgFile.absolutePath);
                holder.thumbnailImageView.setImageBitmap(bmp)
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, CropDetailActivity::class.java)
            intent.putExtra(CropDetailActivity.EXTRA_CROP_DBO_ID, item.id)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: CropItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val thumbnailImageView: ImageView = binding.thumbnailmageView
        val plantNameTextView: TextView = binding.plantName

        init {

        }

        override fun toString(): String {
            return super.toString() + " '" + plantNameTextView.text + "'"
        }

    }

}