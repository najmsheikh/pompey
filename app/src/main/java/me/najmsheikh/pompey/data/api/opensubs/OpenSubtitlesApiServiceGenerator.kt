package me.najmsheikh.pompey.data.api.opensubs

import me.najmsheikh.pompey.BuildConfig
import me.najmsheikh.pompey.data.api.ServiceGenerator
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Najm Sheikh <hello@najmsheikh.me> on 7/29/21.
 */
object OpenSubtitlesApiServiceGenerator : ServiceGenerator {

    override val apiService: OpenSubtitlesApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.OPENSUBTITLES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return@lazy retrofit.create(OpenSubtitlesApiService::class.java)
    }

}