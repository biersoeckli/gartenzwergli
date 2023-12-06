package ch.ost.gartenzwergli.ui.crops.cropDetail

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ch.ost.gartenzwergli.InitialDataLoaderActivity
import ch.ost.gartenzwergli.R
import ch.ost.gartenzwergli.model.dbo.CropDbo
import ch.ost.gartenzwergli.services.DataStorage
import ch.ost.gartenzwergli.services.DatabaseService
import ch.ost.gartenzwergli.ui.crops.addCropToBed.AddCropToBedActivity
import ch.ost.gartenzwergli.ui.crops.titlecase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.CoroutineContext


class CropDetailActivity() : AppCompatActivity(), CoroutineScope {

    private var cropDbo: CropDbo? = null;

    companion object {
        const val EXTRA_CROP_DBO_ID = "CROP_DBO_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_detail)

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.title = "Crop"
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        val intent = intent
        val cropDboId = intent.getStringExtra(EXTRA_CROP_DBO_ID)

        launch {
            val dataStorage = DataStorage()
            dataStorage.syncDetailCropDboIfNeeded(cropDboId!!)
            cropDbo = DatabaseService.getDb().cropDao().findById(cropDboId!!)
            val cropName = cropDbo!!.name.titlecase()

            findViewById<TextView>(R.id.cropTitleTextView).setText(cropName)

            if (actionBar != null) {
                actionBar.title = cropName
            }

            findViewById<TextView>(R.id.cropDescriptionTextView).setText(cropDbo!!.description)

            if (cropDbo!!.thumnailPath != null) {
                val imgFile = File(cropDbo!!.thumnailPath!!);
                if (imgFile.exists()) {
                    val bmp = BitmapFactory.decodeFile(imgFile.absolutePath);
                    findViewById<ImageView>(R.id.cropImageView).setImageBitmap(bmp)
                }
            }
        }

        findViewById<Button>(R.id.addCropToBedButton).setOnClickListener {
            val intent = Intent(this@CropDetailActivity, AddCropToBedActivity::class.java)
            intent.putExtra(AddCropToBedActivity.EXTRA_CROP_DBO_ID, cropDboId)
            this@CropDetailActivity.startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}