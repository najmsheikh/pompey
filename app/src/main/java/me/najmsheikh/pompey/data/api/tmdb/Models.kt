package me.najmsheikh.pompey.data.api.tmdb

import com.google.gson.annotations.SerializedName

data class Result(
    @SerializedName("release_date", alternate = ["first_air_date"]) val releaseDate: String,
    @SerializedName("adult") val isAdult: Boolean,
    @SerializedName("backdrop_path") val backgroundUrl: String,
    @SerializedName("id") val id: Int,
    @SerializedName("genre_ids") val genreIds: List<Int>,
    @SerializedName("original_language") val originalLanguage: String,
    @SerializedName("original_title", alternate = ["original_name"]) val originalTitle: String,
    @SerializedName("poster_path") val posterUrl: String,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("video") val hasVideo: Boolean,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("title", alternate = ["name"]) val title: String,
    @SerializedName("overview") val description: String,
    @SerializedName("popularity") val popularity: Double,
    @SerializedName("media_type") val mediaType: String,
)

data class TrendingResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<Result>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int,
)