package me.najmsheikh.pompey.data.repository

import me.najmsheikh.pompey.data.api.tmdb.Result
import me.najmsheikh.pompey.data.api.tmdb.TmdbApiService
import me.najmsheikh.pompey.data.models.Episode
import me.najmsheikh.pompey.data.models.MediaContent
import me.najmsheikh.pompey.data.models.Movie
import java.time.LocalDate

/**
 * Data repository for all media content.
 */
class MediaRepository(private val tmdbApiService: TmdbApiService) {

    suspend fun getAllTrendingContentForWeek(pageNumber: Int = 1): List<MediaContent> {
        val response = tmdbApiService.getTrending(
            mediaType = "all",
            timeWindow = "week",
            pageNumber = pageNumber
        )

        return response.results.map(this::convertModel)
    }

    suspend fun getMovieDetails(id: String): Movie {
        val response = tmdbApiService.getMovieDetails(id)

        return convertModel(response) as Movie
    }

    private fun convertModel(result: Result): MediaContent {
        return if (result.mediaType == "tv") {
            Episode(
                id = result.id.toString(),
                title = result.title,
                description = result.description,
                tagline = result.tagline,
                posterUrl = "https://image.tmdb.org/t/p/w342${result.posterUrl}",
                backgroundUrl = "https://image.tmdb.org/t/p/original${result.backgroundUrl}",
                releaseDate = result.releaseDate.let { LocalDate.parse(it) },
                recommendations = result.recommendations?.results?.map { rec ->
                    convertModel(rec)
                } ?: emptyList()
            )
        } else {
            Movie(
                id = result.id.toString(),
                title = result.title,
                description = result.description,
                tagline = result.tagline,
                posterUrl = "https://image.tmdb.org/t/p/w342${result.posterUrl}",
                backgroundUrl = "https://image.tmdb.org/t/p/original${result.backgroundUrl}",
                releaseDate = result.releaseDate.let { LocalDate.parse(it) },
                recommendations = result.recommendations?.results?.map { rec ->
                    convertModel(rec)
                } ?: emptyList()
            )
        }
    }
}