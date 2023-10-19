package ch.ost.gartenzwergli.model.dbo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity("crop", indices = [Index(value = ["externalId"], unique = true)])
data class CropDbo(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    val externalId: Int,
    val name: String,
    val description: String?,
    val slug: String?,
    //val alternateNames: List<String>,
    //val scientificNames: List<String>,
    val scientificName: String?,
    val thumbnailUrl: String?,
    var thumnailPath: String?,
)