package ch.ost.gartenzwergli.services

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import ch.ost.gartenzwergli.model.dbo.CropDbo
import ch.ost.gartenzwergli.model.dbo.ParameterDbo
import ch.ost.gartenzwergli.model.dbo.cropevent.CropEventDbo
import ch.ost.gartenzwergli.services.interfaces.AppDatabase
import ch.ost.gartenzwergli.utils.CropDboUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.TimeUnit


class DataStorage() {

    private var okHttpClient: OkHttpClient
    private var db: AppDatabase = DatabaseService.getDb()

    init {
        val okHttpClient = OkHttpClient()
        val okHttpBuilder = okHttpClient.newBuilder()
            .connectTimeout(60000, TimeUnit.SECONDS)
            .readTimeout(60000, TimeUnit.SECONDS)
        this.okHttpClient = okHttpBuilder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun runInitial(ctx: Context) {
        val lastUpdateParamKey = "lastUpdate"
        val lastUpdate = db.parameterDao().findByKey(lastUpdateParamKey)
        if (lastUpdate == null) {
            syncCrops()
            syncImagesForCrops(ctx)
            db.parameterDao().insertAll(
                ParameterDbo(
                    lastUpdateParamKey,
                    LocalDate.now().toString()
                )
            )
        }

    }

    private suspend fun syncImagesForCrops(ctx: Context) {

        withContext(Dispatchers.IO) {
            val allCrops = db.cropDao().getAll()
            allCrops
                .filter { crop -> crop.thumbnailUrl != null }
                .parallelStream()
                .forEach { crop ->

                    val fileName = CropDboUtils.getCropImageFileName(crop)
                    val imagesFolderPath = CropDboUtils.getCropImagesPath(ctx)

                    val imagesFolderPathFile = File(imagesFolderPath)
                    if (!imagesFolderPathFile.isDirectory) {
                        imagesFolderPathFile.mkdirs()
                    }

                    val imageOutputPathFile = File(imagesFolderPath, fileName)
                    if (!imageOutputPathFile.exists()) {
                        val request = Request.Builder().url(crop.thumbnailUrl!!).build()
                        val response = okHttpClient.newCall(request).execute()
                        if (response.isSuccessful) {
                            val output = imageOutputPathFile.outputStream()
                            crop.thumnailPath = imageOutputPathFile.absolutePath
                            output.run {
                                write(response.body()?.bytes())
                                close()
                            }
                        }
                    }
                }
            val cropsWithThumbnailPath = allCrops.filter { crop -> crop.thumnailPath != null }
            db.cropDao().updateAll(*cropsWithThumbnailPath.toTypedArray())
        }
    }

    private suspend fun syncCrops() {
        val response = RestClient.getGrowstuffClient().getCrops()
        if (!response.isSuccessful) {
            throw Exception("Could not fetch crops from growstuff")
        }
        val apiCrops = response.body()
        val existingCropDbos = db.cropDao().getAll()

        val mappedCropDbosFromApi = apiCrops!!
            .filter { crop -> crop != null && crop.id != null }
            .map { crop ->
                CropDbo(
                    UUID.randomUUID().toString(),
                    Integer.parseInt(crop.id!!),
                    crop.name,
                    crop.description,
                    crop.slug,
                    crop.scientific_name,
                    crop.thumbnail_url,
                    null
                )
            }

        // insert all crops
        val newCrops = mappedCropDbosFromApi
            .filter { cropDbo -> existingCropDbos.find { it.externalId == cropDbo.externalId } == null }
        if (!newCrops.isEmpty()) {
            db.cropDao().insertAll(*newCrops.toTypedArray())
        }

        // update existing crops
        val updatedCrops = mappedCropDbosFromApi
            .filter { cropDbo -> existingCropDbos.find { it.externalId == cropDbo.externalId } != null }
        if (!updatedCrops.isEmpty()) {
            // map id of existing crops to updated crops
            updatedCrops.forEach { cropDbo ->
                val existingCrop = existingCropDbos.find { it.externalId == cropDbo.externalId }
                if (existingCrop != null) {
                    cropDbo.id = existingCrop.id
                    cropDbo.thumnailPath = existingCrop.thumnailPath
                }
            }
            db.cropDao().updateAll(*updatedCrops.toTypedArray())
        }
    }

    suspend fun insertCropEvent(cropEvent: CropEventDbo) {
        withContext(Dispatchers.IO) {
            db.cropEventDao().insertAll(cropEvent)
        }
    }
}