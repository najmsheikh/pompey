package me.najmsheikh.pompey.data.api.tmdb

import me.najmsheikh.pompey.data.api.ApiService
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * An [ApiService] to access The Movie Database API.
 */
interface TmdbApiService : ApiService {

    /**
     * Get the daily or weekly trending items. The daily trending list tracks items over the period
     * of a day while items have a 24 hour half life. The weekly list tracks items over a 7 day
     * period, with a 7 day half life.
     *
     * @param mediaType one of either 'all', 'movie', or 'tv
     * @param timeWindow one of 'day' or 'week'
     * @param pageNumber result page number
     */
    @GET("/3/trending/{type}/{window}")
    suspend fun getTrending(
        @Path("type") mediaType: String = "all",
        @Path("window") timeWindow: String = "day",
        @Query("page") pageNumber: Int = 1,
    ): Response

    @GET("/3/movie/{movie_id}?append_to_response=recommendations")
    suspend fun getMovieDetails(@Path("movie_id") movieId: String): Result

}


