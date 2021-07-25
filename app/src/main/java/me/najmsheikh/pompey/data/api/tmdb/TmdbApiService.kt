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

    /**
     * Search for movies and TV shows.
     *
     * @param searchQuery the content to search for
     * @param pageNumber result page number
     * @param includeAdult whether to include adult content in the result
     */
    @GET("/3/search/multi")
    suspend fun searchMultiple(
        @Query("query") searchQuery: String,
        @Query("page") pageNumber: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false,
    ): Response

    /**
     * Get the primary information about a movie by ID.
     *
     * @param movieId the movie ID
     */
    @GET("/3/movie/{movie_id}?append_to_response=recommendations")
    suspend fun getMovieDetails(@Path("movie_id") movieId: String): Result

    /**
     * Get the primary TV show details by ID.
     *
     * @param showId the show ID
     */
    @GET("/3/tv/{tv_id}?append_to_response=recommendations")
    suspend fun getShowDetails(@Path("tv_id") showId: String): Result

    /**
     * Get the TV season details by ID.
     *
     * @param showId the show ID
     * @param seasonNumber the specific season
     */
    @GET("/3/tv/{tv_id}/season/{season_number}")
    suspend fun getShowSeasonDetails(
        @Path("tv_id") showId: String,
        @Path("season_number") seasonNumber: Int,
    ): Result

    /**
     * Get the TV episode details by ID.
     *
     * @param showId the show ID
     * @param seasonNumber the specific season
     * @param episodeNumber the specific episode
     */
    @GET("/3/tv/{tv_id}/season/{season_number}/episode/{episode_number}?append_to_response=external_ids")
    suspend fun getEpisodeDetails(
        @Path("tv_id") showId: String,
        @Path("season_number") seasonNumber: Int,
        @Path("episode_number") episodeNumber: Int,
    ): Result

}
