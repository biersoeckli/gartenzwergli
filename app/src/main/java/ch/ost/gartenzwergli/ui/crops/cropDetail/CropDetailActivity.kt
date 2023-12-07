package ch.ost.gartenzwergli.ui.crops.cropDetail

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
            cropDbo = DatabaseService.getDb().cropDao().findById(cropDboId)
            val cropName = cropDbo!!.name.titlecase()

            findViewById<TextView>(R.id.cropTitleTextView).setText(cropName)

            if (actionBar != null) {
                actionBar.title = cropName
            }

            var scientificName = ""
            if (cropDbo!!.scientificName != null)
                scientificName = cropDbo!!.scientificName!!
            findViewById<TextView>(R.id.cropSubtitleScientificNameTextView).text = scientificName

            var alternateNames = ""
            if (cropDbo!!.alternateNames != null)
                alternateNames = cropDbo!!.alternateNames!!.joinToString(", ")
            findViewById<TextView>(R.id.cropSubtitleAlternateNamesTextView).text = alternateNames

            var description = "-"
            if (cropDbo!!.description != null)
                description = cropDbo!!.description!!
            findViewById<TextView>(R.id.cropDescriptionTextView).text = description

            var medianDaysForFirstHarvest = "-"
            if (cropDbo!!.medianDaysForFirstHarvest != null)
                medianDaysForFirstHarvest = cropDbo!!.medianDaysForFirstHarvest!!.toString() + " days until harvest"
            findViewById<TextView>(R.id.medianDaysForFirstHarvestTextView).text = medianDaysForFirstHarvest

            var medianDaysToLastHarvest = "-"
            if (cropDbo!!.medianDaysToLastHarvest != null)
                medianDaysToLastHarvest = cropDbo!!.medianDaysToLastHarvest!!.toString() + " days until last harvest"
            findViewById<TextView>(R.id.medianDaysToLastHarvestTextView).text = medianDaysToLastHarvest

            var medianLifespan = "-"
            if (cropDbo!!.medianLifespan != null)
                medianLifespan = cropDbo!!.medianLifespan!!.toString() + " days"
            findViewById<TextView>(R.id.medianLifespanTextView).text = medianLifespan

            var sowingMethod = "-"
            if (cropDbo!!.sowingMethod != null)
                sowingMethod = cropDbo!!.sowingMethod!!
            findViewById<TextView>(R.id.sowingMethodTextView).text = sowingMethod

            var sunRequirements = "-"
            if (cropDbo!!.sunRequirements != null)
                sunRequirements = cropDbo!!.sunRequirements!!
            findViewById<TextView>(R.id.sunRequirementsTextView).text = sunRequirements

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