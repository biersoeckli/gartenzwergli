package ch.ost.gartenzwergli.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import ch.ost.gartenzwergli.data.CropEventDao
import ch.ost.gartenzwergli.model.dbo.cropevent.CropEventAndCrop
import ch.ost.gartenzwergli.model.dbo.cropevent.CropEventDbo
import kotlinx.coroutines.flow.Flow

class CropEventRepository(private val cropEventDao: CropEventDao) {

    val cropEventAndCrops: Flow<List<CropEventAndCrop>> = cropEventDao.getAllCropEventAndCrops()

    @WorkerThread
    suspend fun insert(cropEvent: CropEventDbo) {
        cropEventDao.insertAll(cropEvent)
    }

    @WorkerThread
    fun delete(cropEvent: CropEventDbo) {
        cropEventDao.delete(cropEvent)
    }
}