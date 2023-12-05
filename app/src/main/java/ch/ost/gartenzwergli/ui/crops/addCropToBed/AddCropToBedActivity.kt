package ch.ost.gartenzwergli.ui.crops.addCropToBed

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ch.ost.gartenzwergli.R
import ch.ost.gartenzwergli.model.dbo.CropDbo
import ch.ost.gartenzwergli.model.dbo.cropevent.CropEventDbo
import ch.ost.gartenzwergli.services.DataStorage
import ch.ost.gartenzwergli.services.DatabaseService
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

            findViewById<Button>(R.id.saveCropEventButton).setOnClickListener {
                val datePicker = findViewById<DatePicker>(R.id.cropDatePicker)
                val day: Int = datePicker.dayOfMonth
                val month: Int = datePicker.month + 1
                val year: Int = datePicker.year

                // convert to localdate
                val dateString = LocalDate.of(year, month, day).format(DateTimeFormatter.ISO_DATE)

                DatabaseService.getDb().cropEventDao().insertAll(
                    CropEventDbo(
                        title = cropDbo.name,
                        description = cropDbo.description,
                        dateTime = dateString,
                        cropId = cropDboId
                    )
                )

                /*
                val snackbar = Snackbar.make(
                    findViewById(R.id.addCropToBedButton),
                    "Crop Event saved",
                    Snackbar.LENGTH_LONG
                )
                snackbar.show()*/
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}
