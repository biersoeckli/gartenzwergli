package ch.ost.gartenzwergli.services

import ch.ost.gartenzwergli.services.interfaces.GrowstuffApi
import ch.ost.gartenzwergli.services.interfaces.OpenFarmApi
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit



class RestClient {
    companion object{
        private var retrofit:Retrofit?=null
        fun getApiClient(baseUrl: String): Retrofit {
            val gson = GsonBuilder()
                .setLenient()
                .create()
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(100, TimeUnit.SECONDS)
                .connectTimeout(100, TimeUnit.SECONDS)
                .build()
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }

        fun getGrowstuffClient(): GrowstuffApi {
            return getApiClient("https://www.growstuff.org/").create(GrowstuffApi::class.java)
        }
        fun getOpenFarmClient(): OpenFarmApi {
            return getApiClient("https://openfarm.cc/api/v1/").create(OpenFarmApi::class.java)
        }
    }

}