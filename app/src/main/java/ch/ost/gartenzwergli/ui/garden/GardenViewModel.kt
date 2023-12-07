package ch.ost.gartenzwergli.ui.garden

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import ch.ost.gartenzwergli.model.dbo.cropevent.CropEventAndCrop
import ch.ost.gartenzwergli.model.dbo.cropevent.CropEventDbo
import ch.ost.gartenzwergli.repository.CropEventRepository

class GardenViewModel(private val cropEventRepository: CropEventRepository) : ViewModel() {

    val cropEventAndCrops: LiveData<List<CropEventAndCrop>> = cropEventRepository.cropEventAndCrops.asLiveData()

    suspend fun insert(cropEvent: CropEventDbo) {
        cropEventRepository.insert(cropEvent)
    }

    fun delete(cropEvent: CropEventDbo) {
        cropEventRepository.delete(cropEvent)
    }
}

class GardenViewModelFactory(private val cropEventRepository: CropEventRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GardenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GardenViewModel(cropEventRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}