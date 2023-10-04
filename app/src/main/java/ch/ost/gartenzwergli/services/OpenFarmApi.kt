package ch.ost.gartenzwergli.services

import ch.ost.gartenzwergli.model.GrowstuffCropDto
import ch.ost.gartenzwergli.model.openfarm.OpenFarmCropDataDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenFarmApi {
    @GET("crops/{id}")
    fun getCrop(@Path("id") openFarmId: String): Call<OpenFarmCropDataDto>
}