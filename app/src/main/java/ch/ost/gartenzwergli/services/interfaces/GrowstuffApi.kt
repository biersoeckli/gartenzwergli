package ch.ost.gartenzwergli.services.interfaces

import ch.ost.gartenzwergli.model.dto.growstuff.GrowstuffCropDetailDto
import ch.ost.gartenzwergli.model.dto.growstuff.GrowstuffCropDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GrowstuffApi {

    @GET("crops.json")
    suspend fun getCrops(@Query("page") pageNumber: Int = 0): Response<List<GrowstuffCropDto>>

    @GET("crops/{id}.json")
    suspend fun getCrop(@Path("id") id: Int): Response<GrowstuffCropDetailDto>
}