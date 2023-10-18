package ch.ost.gartenzwergli.model.dbo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity("parameter")
data class ParameterDbo(
    @PrimaryKey val id: String,
    val value: String,
)
