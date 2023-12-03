package ch.ost.gartenzwergli.ui.crops.addCropToBed

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import ch.ost.gartenzwergli.R
import ch.ost.gartenzwergli.model.dbo.CropDbo
import ch.ost.gartenzwergli.services.DataStorage
import ch.ost.gartenzwergli.services.DatabaseService
import ch.ost.gartenzwergli.ui.crops.cropDetail.CropDetailActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AddCropToBedActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var cropDbo: CropDbo

    companion object {
        const val EXTRA_CROP_DBO_ID = "CROP_DBO_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_crop_to_bed)
        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.title = "Add Crop To Bed"
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        val intent = intent
        val cropDboId = intent.getStringExtra(EXTRA_CROP_DBO_ID)

        launch {
            val dataStorage = DataStorage()
            dataStorage.syncDetailCropDboIfNeeded(cropDboId!!)
            cropDbo = DatabaseService.getDb().cropDao().findById(cropDboId)
            findViewById<TextView>(R.id.cropTitleTextView).setText(cropDbo!!.name)

        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}
