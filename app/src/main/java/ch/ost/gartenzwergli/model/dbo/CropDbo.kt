package ch.ost.gartenzwergli.model.dbo

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

val DUMMY_CROP_ID = "0";

@Entity("crop", indices = [Index(value = ["externalId"], unique = true)])
data class CropDbo(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    val externalId: Int,
    val name: String,
    val description: String?,
    val slug: String?,
    val alternateNames: List<String>,
    val scientificName: String?,
    val thumbnailUrl: String?,
    var thumnailPath: String?,
    var height: Double?,
    var spread: Double?,
    var medianDaysForFirstHarvest: Int?,
    var medianDaysToLastHarvest: Int?,
    var medianLifespan: Int?,
    var sowingMethod: String?,
    var sunRequirements: String?,
    var detailsFetched: Boolean = false
)