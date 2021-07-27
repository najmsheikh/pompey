package me.najmsheikh.pompey.data.api.sourcerer

import me.najmsheikh.pompey.BuildConfig
import me.najmsheikh.pompey.data.api.ServiceGenerator
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SourcererApiServiceGenerator : ServiceGenerator {

    override val apiService: SourcererApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.SOURCERER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return@lazy retrofit.create(SourcererApiService::class.java)
    }

}
