package me.najmsheikh.pompey.data.api.tmdb

import me.najmsheikh.pompey.BuildConfig
import me.najmsheikh.pompey.data.api.ServiceGenerator
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * A [ServiceGenerator] for The Movie Database API.
 *
 * @see TmdbApiService
 */
object TmdbApiServiceGenerator : ServiceGenerator {

    private val okHttpClient: OkHttpClient by lazy {
        return@lazy OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val originalUrl = originalRequest.url()

                val authenticatedUrl = originalUrl.newBuilder()
                    .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
                    .build()
                val authenticatedRequest = originalRequest.newBuilder()
                    .url(authenticatedUrl)
                    .build()

                chain.proceed(authenticatedRequest)
            }
            .build()
    }

    override val apiService: TmdbApiService by lazy {
        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.TMDB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return@lazy retrofit.create(TmdbApiService::class.java)
    }
}