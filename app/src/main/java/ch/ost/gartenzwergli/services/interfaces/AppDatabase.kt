package ch.ost.gartenzwergli.services.interfaces

import androidx.room.Database
import androidx.room.RoomDatabase
import ch.ost.gartenzwergli.data.CropDao
import ch.ost.gartenzwergli.data.ParameterDao
import ch.ost.gartenzwergli.model.dbo.CropDbo
import ch.ost.gartenzwergli.model.dbo.ParameterDbo

@Database(entities = [CropDbo::class, ParameterDbo::class],
    version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cropDao(): CropDao
    abstract fun parameterDao(): ParameterDao
}