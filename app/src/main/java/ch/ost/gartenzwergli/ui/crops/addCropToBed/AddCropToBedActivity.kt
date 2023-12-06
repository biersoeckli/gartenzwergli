package ch.ost.gartenzwergli.ui.crops.addCropToBed

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import ch.ost.gartenzwergli.R
import ch.ost.gartenzwergli.model.dbo.CropDbo
import ch.ost.gartenzwergli.model.dbo.cropevent.CropEventDbo
import ch.ost.gartenzwergli.services.DataStorage
import ch.ost.gartenzwergli.services.DatabaseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import scheduleCropNotification
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
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

                val cropEventId = UUID.randomUUID().toString()
                DatabaseService.getDb().cropEventDao().insertAll(
                    CropEventDbo(
                        title = cropDbo.name,
                        description = cropDbo.scientificName ?: "",
                        dateTime = dateString,
                        cropId = cropDboId,
                        id = cropEventId
                    )
                )

                /*
                val snackbar = Snackbar.make(
                    findViewById(R.id.addCropToBedButton),
                    "Crop Event saved",
                    Snackbar.LENGTH_LONG
                )
                snackbar.show()*/

                createNotificationChannelIfNotExists()
                scheduleCropNotification(
                    context = this@AddCropToBedActivity,
                    DatabaseService.getDb().cropEventDao().getById(cropEventId)
                )
                /*
                if (cropDbo.medianDaysForFirstHarvest != null) {
                    val service = Executors.newSingleThreadScheduledExecutor()
                    val handler = Handler(Looper.getMainLooper())
                    service.schedule({
                        handler.post {
                            showNotification(cropDbo)
                        }
                    //}, cropDbo.medianDaysForFirstHarvest!!.toLong(), TimeUnit.DAYS)
                    }, 10, TimeUnit.SECONDS)
                }*/
                finish()
            }
        }
    }


    private val notificationsPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->


    }

    private fun ensureNotificationsPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true

        val permission = Manifest.permission.POST_NOTIFICATIONS
        val granted = ContextCompat.checkSelfPermission(
            applicationContext, permission
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) return true

        if (shouldShowRequestPermissionRationale(permission)) {

            notificationsPermissionLauncher.launch(permission)

            return false
        }

        notificationsPermissionLauncher.launch(permission)

        return false
    }

    private fun createNotificationChannelIfNotExists() {

        ensureNotificationsPermission()

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        val name = getString(R.string.default_notification_channel_name)
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelExists = notificationManager.notificationChannels.any {
            it.name == name
        }
        if (channelExists) return

        val descriptionText = "Notification when crop is ready to harvest"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(name, name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system.
        notificationManager.createNotificationChannel(channel)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}
