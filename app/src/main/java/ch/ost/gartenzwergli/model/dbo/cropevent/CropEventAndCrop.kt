package ch.ost.gartenzwergli.model.dbo.cropevent

import androidx.room.Embedded
import androidx.room.Relation
import ch.ost.gartenzwergli.model.dbo.CropDbo

data class CropEventAndCrop(
    @Embedded val cropEvent: CropEventDbo,
    @Relation(
        parentColumn = "cropId",
        entityColumn = "id"
    )
    val crop: CropDbo? = null
)