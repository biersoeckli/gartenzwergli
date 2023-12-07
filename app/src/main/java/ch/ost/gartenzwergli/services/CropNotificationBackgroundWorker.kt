import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import androidx.work.WorkerParameters
import ch.ost.gartenzwergli.MainActivity
import ch.ost.gartenzwergli.R
import ch.ost.gartenzwergli.model.dbo.cropevent.CropEventAndCrop
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.TimeUnit

class CropNotificationBackgroundWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    private val CHANNEL_ID = "Crop Updates";
    override fun doWork(): Result {
        // Do the work here--in this case, create a notification.
        showNotification()
        // Indicate whether the task finished successfully with the Result
        return Result.success()
    }

    private fun showNotification() {
        val cropName = this.inputData.getString("cropName")
        val cropDescription = this.inputData.getString("cropDescription")

        val notificationId = Math.random().toInt()

        // Erstelle eine Instanz von NotificationManagerCompat
        val notificationManager = NotificationManagerCompat.from(applicationContext)

        var contentText = "Your $cropName is ready to harvest!"
        if (cropDescription.equals("Last Harvest")) {
            contentText = "Your $cropName is ready to harvest a last time!"
        }

        // Erstelle eine Instanz von NotificationCompat.Builder
        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_calendar_month_black_24dp)
            .setContentTitle("Harvest your $cropName!")
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        var flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK;
        // Erstelle eine Instanz von Intent
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK;
        }

        // Erstelle eine Instanz von PendingIntent
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Füge die Aktionen hinzu
        notificationBuilder.addAction(R.mipmap.ic_launcher_round, "open", pendingIntent)

        // Zeige die Benachrichtigung an
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}

fun scheduleCropNotification(context: Context, cropAndEventDbo: CropEventAndCrop) {
    val workManager = WorkManager.getInstance(context)

    val notificationDate = LocalDate.parse(cropAndEventDbo.cropEvent.dateTime, DateTimeFormatter.ISO_DATE)
    val daysBetweenNowAndNotificationDate = LocalDate.now().until(notificationDate).days

    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val data = Data.Builder()
    data.putString("cropName", cropAndEventDbo.cropEvent.title)
    data.putString("cropDescription", cropAndEventDbo.cropEvent.description)

    val notificationWorkRequest = OneTimeWorkRequestBuilder<CropNotificationBackgroundWorker>()
        .setConstraints(constraints)
        .setInputData(data.build())
        .setId(UUID.fromString(cropAndEventDbo.cropEvent.id))
        .setInitialDelay(daysBetweenNowAndNotificationDate.toLong(), TimeUnit.DAYS)
        .build()
    workManager.enqueue(notificationWorkRequest)
    Log.d("Notification", "Notification for ${cropAndEventDbo.cropEvent.id} scheduled in ${daysBetweenNowAndNotificationDate} days")
}

fun cancelNotificationForCropEvent(ctx: Context, cropEventDboId: String) {
    val workManager = WorkManager.getInstance(ctx)
    workManager.cancelWorkById(UUID.fromString(cropEventDboId))
    Log.d("Notification", "Notification for $cropEventDboId cancelled")
}
