package ch.ost.gartenzwergli.services.interfaces

import ch.ost.gartenzwergli.model.GrowstuffCropDto
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GrowstuffApi {
    @GET("crops.json")
    suspend fun getCrops(): Response<List<GrowstuffCropDto>>

    @GET("crops/{id}.json")
    suspend fun getCrop(@Path("id") id: String): Response<List<GrowstuffCropDto>>
}