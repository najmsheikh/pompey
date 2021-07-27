package me.najmsheikh.pompey.data.api.sourcerer

import me.najmsheikh.pompey.data.api.ApiService
import retrofit2.http.GET
import retrofit2.http.Path

interface SourcererApiService : ApiService {

    @GET("movie/{imdb_id}.json")
    suspend fun getMovieSources(@Path("imdb_id") imdbId: String): Response

    @GET("series/{imdb_id}:{s}:{e}.json")
    suspend fun getShowSources(
        @Path("imdb_id") imdbId: String,
        @Path("s") seasonNumber: Int,
        @Path("e") episodeNumber: Int,
    ): Response

}
