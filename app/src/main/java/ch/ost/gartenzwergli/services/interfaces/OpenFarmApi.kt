package ch.ost.gartenzwergli.services.interfaces

import ch.ost.gartenzwergli.model.dto.openfarm.OpenFarmCropDataDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenFarmApi {
    @GET("crops/{id}")
    suspend fun getCrop(@Path("id") openFarmId: String): Response<OpenFarmCropDataDto>
}