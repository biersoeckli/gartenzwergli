package ch.ost.gartenzwergli.model.dbo.cropevent

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar
import java.util.UUID

@Entity("crop_event")
data class CropEventDbo(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var title: String,
    val description: String?,
    val dateTime: String,
    val plantedTime: String,
    val cropId: String,
)
