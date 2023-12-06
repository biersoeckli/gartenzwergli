package ch.ost.gartenzwergli.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ch.ost.gartenzwergli.model.dbo.cropevent.CropEventAndCrop
import ch.ost.gartenzwergli.model.dbo.cropevent.CropEventDbo

@Dao
interface CropEventDao {

    @Query("SELECT * FROM crop_event")
    fun getAll(): List<CropEventDbo>

    @Query("SELECT * FROM crop_event")
    fun getAllCropEventAndCrops(): List<CropEventAndCrop>

    @Query("SELECT * FROM crop_event WHERE id = :id")
    fun getById(id: String): CropEventAndCrop

    @Transaction
    @Query("SELECT * FROM crop_event WHERE date(dateTime) = :date")
    fun getCropEventsAndCropsByDate(date: String): List<CropEventAndCrop>

    @Insert
    fun insertAll(vararg cropEvents: CropEventDbo)

    @Update
    fun updateAll(vararg cropEvents: CropEventDbo)

    @Delete
    fun delete(cropEvent: CropEventDbo)
}