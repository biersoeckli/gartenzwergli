package ch.ost.gartenzwergli.services

import ch.ost.gartenzwergli.model.CropDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GrowstuffApi {
    @GET("crops.json")
    fun getCrops(): Call<List<CropDto>>

    @GET("crops/{id}.json")
    fun getCrop(@Path("id") id: String): Call<List<CropDto>>
}