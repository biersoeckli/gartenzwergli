package ch.ost.gartenzwergli.ui.crops.addCropToBed

import android.Manifest
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import ch.ost.gartenzwergli.MainActivity
import ch.ost.gartenzwergli.R
import ch.ost.gartenzwergli.databinding.ActivityAddCropToBedBinding
import ch.ost.gartenzwergli.model.dbo.CropDbo
import ch.ost.gartenzwergli.model.dbo.cropevent.CropEventDbo
import ch.ost.gartenzwergli.services.DataStorage
import ch.ost.gartenzwergli.services.DatabaseService
import ch.ost.gartenzwergli.ui.crops.titlecase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import scheduleCropNotification
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.UUID
import kotlin.coroutines.CoroutineContext

class AddCropToBedActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var cropDbo: CropDbo
    private var medianDaysToLastHarvest: Int = 0
    private var medianDaysForFirstHarvest: Int = 0

    private var _binding: ActivityAddCropToBedBinding? = null

    private val binding get() = _binding!!

    private val calendar: Calendar = Calendar.getInstance()
    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    companion object {
        const val EXTRA_CROP_DBO_ID = "CROP_DBO_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddCropToBedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val localDateFormatted = LocalDate.now().format(formatter)
        binding.addCropCalendarText.setText(localDateFormatted)
        binding.addCropCropSewDateTextView.text = "Sew date: ${localDateFormatted}"

        binding.addCropCalendarText.setOnClickListener {
            showDatePickerDialog(this)
        }

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        val intent = intent
        val cropDboId = intent.getStringExtra(EXTRA_CROP_DBO_ID)

        launch {
            val dataStorage = DataStorage()
            dataStorage.syncDetailCropDboIfNeeded(cropDboId!!)
            cropDbo = DatabaseService.getDb().cropDao().findById(cropDboId)

            medianDaysToLastHarvest = cropDbo.medianDaysToLastHarvest!!
            medianDaysForFirstHarvest = cropDbo.medianDaysForFirstHarvest!!

            setCropEventInfo(LocalDate.now())

            val cropName = cropDbo.name.titlecase() ?: "Crop"
            findViewById<TextView>(R.id.cropTitleTextView).text = cropName

            if (actionBar != null) {
                actionBar.title = "Add ${cropName} to your garden"
            }

            binding.addCropCropNameTextView.text = cropName

            findViewById<Button>(R.id.saveCropEventButton).setOnClickListener {
                // get text from edit text and convert to java.time LocalDate
                val dateString = binding.addCropCalendarText.text.toString()
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

                // convert to localdate
                val localDate = LocalDate.parse(dateString, formatter)
                val localDateISO = localDate.format(DateTimeFormatter.ISO_DATE)

                var cropEventId = UUID.randomUUID().toString()
                insertCropEvent(CropEventDbo(
                    title = cropDbo.name,
                    description = "Sewed",
                    dateTime = localDateISO,
                    plantedTime = localDateISO,
                    cropId = cropDboId,
                    id = cropEventId
                ))

                if (medianDaysForFirstHarvest > 0) {
                    val firstHarvestLocalDate = localDate.plusDays(medianDaysForFirstHarvest.toLong())
                    val firstHarvestLocalDateISO = firstHarvestLocalDate.format(DateTimeFormatter.ISO_DATE)

                    // create first harvest event if medianDaysForFirstHarvest is set
                    cropEventId = UUID.randomUUID().toString()
                    insertCropEvent(CropEventDbo(
                        title = cropDbo.name,
                        description = "First Harvest",
                        dateTime = firstHarvestLocalDateISO,
                        plantedTime = localDateISO,
                        cropId = cropDboId,
                        id = cropEventId
                    ))
                }

                if (medianDaysToLastHarvest > 0) {
                    val lastHarvestLocalDate = localDate.plusDays(medianDaysToLastHarvest.toLong())
                    val lastHarvestLocalDateISO = lastHarvestLocalDate.format(DateTimeFormatter.ISO_DATE)

                    // create last harvest event if medianDaysToLastHarvest is set
                    cropEventId = UUID.randomUUID().toString()
                    insertCropEvent(CropEventDbo(
                        title = cropDbo.name,
                        description = "Last Harvest",
                        dateTime = lastHarvestLocalDateISO,
                        plantedTime = localDateISO,
                        cropId = cropDboId,
                        id = cropEventId
                    ))
                }


                createNotificationChannelIfNotExists()

                if (binding.addCropGetNotifiedSwitch.isChecked) {
                    if (binding.addCropReadyToHarvestSwitch.isChecked && medianDaysForFirstHarvest > 0) {
                        scheduleCropNotification(
                            context = this@AddCropToBedActivity,
                            DatabaseService.getDb().cropEventDao().getById(cropEventId)
                        )
                    }

                    if (binding.addCropLastHarvestSwitch.isChecked && medianDaysToLastHarvest > 0) {
                        scheduleCropNotification(
                            context = this@AddCropToBedActivity,
                            DatabaseService.getDb().cropEventDao().getById(cropEventId)
                        )
                    }
                }

                val intentToHome = Intent(this@AddCropToBedActivity, MainActivity::class.java)
                supportNavigateUpTo(intentToHome)
                finish()
            }
        }
    }

    private fun insertCropEvent(cropEvent: CropEventDbo) {
        launch {
            DatabaseService.getDb().cropEventDao().insertAll(cropEvent)
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

    private fun showDatePickerDialog(context: Context) {
        // get current date
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        // create date picker dialog
        val datePickerDialog = DatePickerDialog(
            context,
            { view, year, month, dayOfMonth ->
                // show leading zero for single digit days
                var dayOfMonthString = dayOfMonth.toString()
                if (dayOfMonth < 10) {
                    dayOfMonthString = "0${dayOfMonth}"
                }
                var monthString = (month + 1).toString()
                if ((month + 1) < 10) {
                    monthString = "0${month + 1}"
                }
                binding.addCropCalendarText.setText("${dayOfMonthString}.${monthString}.${year}")
                binding.addCropCropSewDateTextView.text = "Sew date: ${dayOfMonthString}.${monthString}.${year}"

                // get text from edit text and convert to java.time LocalDate
                val dateString = binding.addCropCalendarText.text.toString()

                // convert to localdate
                val localDate = LocalDate.parse(dateString, formatter)
                setCropEventInfo(localDate)
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.show()
    }

    private fun setCropEventInfo(localDate: LocalDate) {
        if (medianDaysForFirstHarvest > 0) {
            binding.addCropCropFirstHarvestDateTextView.text =
                "Harvest date: ${localDate.plusDays(cropDbo.medianDaysForFirstHarvest!!.toLong()).format(formatter)}"
        }
        if (medianDaysToLastHarvest > 0) {
            binding.addCropCropLastHarvestDateTextView.text =
                "Last harvest date: ${localDate.plusDays(cropDbo.medianDaysToLastHarvest!!.toLong()).format(formatter)}"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
}
