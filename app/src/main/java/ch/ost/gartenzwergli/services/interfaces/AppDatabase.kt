package ch.ost.gartenzwergli.services.interfaces

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ch.ost.gartenzwergli.data.CropDao
import ch.ost.gartenzwergli.data.CropEventDao
import ch.ost.gartenzwergli.data.ParameterDao
import ch.ost.gartenzwergli.model.converters.Converters
import ch.ost.gartenzwergli.model.dbo.CropDbo
import ch.ost.gartenzwergli.model.dbo.ParameterDbo
import ch.ost.gartenzwergli.model.dbo.cropevent.CropEventDbo

@Database(
    entities = [CropDbo::class, ParameterDbo::class, CropEventDbo::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cropDao(): CropDao
    abstract fun parameterDao(): ParameterDao

    abstract fun cropEventDao(): CropEventDao
}