package me.najmsheikh.pompey.data.repository

import me.najmsheikh.pompey.data.api.tmdb.TmdbApiService
import me.najmsheikh.pompey.data.models.Video
import java.time.LocalDate

/**
 * Data repository for all video content.
 */
class VideoRepository(private val tmdbApiService: TmdbApiService) {

    suspend fun getAllTrendingContentForWeek(pageNumber: Int = 1): List<Video> {
        val response = tmdbApiService.getTrending(
            mediaType = "all",
            timeWindow = "week",
            pageNumber = pageNumber
        )

        return response.results.map { result ->
            return@map Video(
                id = result.id.toString(),
                title = result.title,
                description = result.description,
                posterUrl = "https://image.tmdb.org/t/p/w342${result.posterUrl}",
                backgroundUrl = "https://image.tmdb.org/t/p/original${result.backgroundUrl}",
                releaseDate = result.releaseDate.let { LocalDate.parse(it) }
            )
        }
    }

}