package me.najmsheikh.pompey.data.api.opensubs

import me.najmsheikh.pompey.data.api.ApiService
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenSubtitlesApiService : ApiService {

    @GET("q.json")
    suspend fun getSubtitles(@Query("b") query: String): Response

}
