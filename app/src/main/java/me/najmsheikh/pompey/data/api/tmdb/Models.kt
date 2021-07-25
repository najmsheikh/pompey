package me.najmsheikh.pompey.data.api.tmdb

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("release_date",
        alternate = ["first_air_date", "air_date"]) val releaseDate: String?,
    @SerializedName("backdrop_path") val backgroundUrl: String?,
    @SerializedName("id") val id: Int,
    @SerializedName("imdb_id") val imdbId: String?,
    @SerializedName("poster_path", alternate = ["still_path"]) val posterUrl: String,
    @SerializedName("title", alternate = ["name"]) val title: String,
    @SerializedName("overview") val description: String,
    @SerializedName("tagline") val tagline: String?,
    @SerializedName("media_type", alternate = ["type"]) val mediaType: String,
    @SerializedName("recommendations") val recommendations: Response?,
    @SerializedName("seasons") val seasons: List<Result>?,
    @SerializedName("season_number") val seasonNumber: Int?,
    @SerializedName("episodes") val episodes: List<Result>?,
    @SerializedName("episode_number") val episodeNumber: Int?,
)

data class Response(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<Result>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int,
)
