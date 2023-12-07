package ch.ost.gartenzwergli.services

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import ch.ost.gartenzwergli.model.dbo.CropDbo
import ch.ost.gartenzwergli.model.dbo.DUMMY_CROP_ID
import ch.ost.gartenzwergli.model.dbo.ParameterDbo
import ch.ost.gartenzwergli.model.dbo.cropevent.CropEventDbo
import ch.ost.gartenzwergli.model.dto.growstuff.GrowstuffCropDto
import ch.ost.gartenzwergli.services.interfaces.AppDatabase
import ch.ost.gartenzwergli.utils.CropDboUtils
import com.google.gson.internal.LinkedTreeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.time.LocalDate
import java.util.UUID
import java.util.concurrent.TimeUnit


class DataStorage() {

    private val lastUpdateParamKey = "lastUpdate"

    private var okHttpClient: OkHttpClient
    private var db: AppDatabase = DatabaseService.getDb()

    init {
        val okHttpClient = OkHttpClient()
        val okHttpBuilder = okHttpClient.newBuilder()
            .connectTimeout(60000, TimeUnit.SECONDS)
            .readTimeout(60000, TimeUnit.SECONDS)
        this.okHttpClient = okHttpBuilder.build()
    }

    suspend fun isInitialRunNecessary(): Boolean {
        val lastUpdate = db.parameterDao().findByKey(lastUpdateParamKey)
        return lastUpdate == null;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun runInitial(ctx: Context) {
        if (isInitialRunNecessary()) {
            syncAllCrops()
            createDummyCropIfNotExists()
            syncImagesForCrops(ctx)
            db.parameterDao().insertAll(
                ParameterDbo(
                    lastUpdateParamKey,
                    LocalDate.now().toString()
                )
            )
        }
    }

     fun createDummyCropIfNotExists() {
        val dummyCrop = db.cropDao().findById(DUMMY_CROP_ID)
        if (dummyCrop == null) {
            db.cropDao().insertAll(
                CropDbo(
                    DUMMY_CROP_ID,
                    0,
                    "Dummy Crop",
                    null,
                    null,
                    emptyList(),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                )
            )
        }
    }

    private suspend fun syncImagesForCrops(ctx: Context) {

        Log.d("Crop Sync", "Loading images for crops...")
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
            Log.d("Crop Sync", "Successfully loaded ${cropsWithThumbnailPath.size} crop images.")
        }
    }

    private suspend fun syncAllCrops() {
        val existingCropDbos = db.cropDao().getAll()

        var totalCropCount = 0;
        var pageCounter = 0;
        while (true) {
            pageCounter++
            val response = RestClient.getGrowstuffClient().getCrops(pageCounter)
            if (!response.isSuccessful) {
                break;
            }
            val apiCrops = response.body()
            if (apiCrops.isNullOrEmpty()) {
                break;
            }
            Log.d("Crop Sync", "Processing page $pageCounter")
            totalCropCount += apiCrops.size
            syncCrops(apiCrops, existingCropDbos)
        }
        Log.d("Crop Sync", "Total crops synched: $totalCropCount")
    }

    private fun syncCrops(
        apiCrops: List<GrowstuffCropDto>?,
        existingCropDbos: List<CropDbo>
    ) {
        val mappedCropDbosFromApi = apiCrops!!
            .filter { crop -> crop != null && crop.id != null }
            .map { crop ->
                CropDbo(
                    UUID.randomUUID().toString(),
                    Integer.parseInt(crop.id!!),
                    crop.name,
                    crop.description,
                    crop.slug,
                    crop.alternate_names,
                    crop.scientific_name,
                    crop.thumbnail_url,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
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

    suspend fun syncDetailCropDboIfNeeded(cropDboId: String) {
        val crop = db.cropDao().findById(cropDboId)
        if (crop.detailsFetched) {
            return
        }
        addCropDetailData(crop)
        db.cropDao().updateAll(crop)
    }

    suspend fun syncDetailDataFromNewCropDbos() {
        Log.d("Crop Sync", "Loading detail data for all crops...")
        val allCropDbos = db.cropDao().getAllWithoutDetailData()
        var index = 0;
        allCropDbos.forEach { cropDbo ->
            if (index % 30 == 0 || index == allCropDbos.size) {
                Log.d("Crop Sync", "Loading detail data for crop ($index/${allCropDbos.size})")
            }
            addCropDetailData(cropDbo)
            db.cropDao().updateAll(cropDbo)
            index++
        }
        Log.d("Crop Sync", "Successfully loaded detail data for ${allCropDbos.size} crops.")
    }

    private suspend fun addCropDetailData(cropDbo: CropDbo) {
        try {

            val cropDetailResponse = RestClient.getGrowstuffClient().getCrop(cropDbo.externalId)
            if (cropDetailResponse.isSuccessful) {
                val cropDetailDto = cropDetailResponse.body()
                if (cropDetailDto != null) {
                    // field openfarm_data can be boolean = false if no openfarm data is available^^ wtf
                    if (cropDetailDto.openfarm_data is LinkedTreeMap<*, *>) {
                        val attributes =
                            cropDetailDto.openfarm_data["attributes"] as LinkedTreeMap<*, *>
                        cropDbo.height = attributes["height"] as Double?;
                        cropDbo.spread = attributes["spread"] as Double?;
                        cropDbo.sowingMethod = attributes["sowing_method"] as String?;
                        cropDbo.sunRequirements = attributes["sun_requirements"] as String?;
                    }
                    cropDbo.medianDaysForFirstHarvest = cropDetailDto.median_days_to_first_harvest
                    cropDbo.medianDaysToLastHarvest = cropDetailDto.median_days_to_last_harvest
                    cropDbo.medianLifespan = cropDetailDto.median_lifespan
                    cropDbo.detailsFetched = true
                }
            }
        } catch (ex: Exception) {
            Log.e("Crop Sync", "Error while loading crop detail data for crop ${cropDbo.name}", ex)
        }
    }
}