package me.najmsheikh.pompey.data.repository

import me.najmsheikh.pompey.data.api.tmdb.Result
import me.najmsheikh.pompey.data.api.tmdb.TmdbApiService
import me.najmsheikh.pompey.data.models.MediaContent
import me.najmsheikh.pompey.data.models.Movie
import me.najmsheikh.pompey.data.models.Show
import me.najmsheikh.pompey.data.models.Show.Season
import me.najmsheikh.pompey.data.models.Show.Season.Episode
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

        return response.results.map(this::convertMediaModel)
    }

    suspend fun getMovieDetails(id: String): Movie {
        val response = tmdbApiService.getMovieDetails(id)

        return convertMediaModel(response) as Movie
    }

    suspend fun getShowDetails(id: String): Show {
        val showResponse = tmdbApiService.getShowDetails(id)

        return convertMediaModel(showResponse) as Show
    }

    private fun convertMediaModel(result: Result): MediaContent {
        return if (result.mediaType == "tv" || !result.seasons.isNullOrEmpty()) {
            Show(
                id = result.id.toString(),
                imdbId = result.imdbId,
                title = result.title,
                description = result.description,
                tagline = result.tagline,
                posterUrl = "https://image.tmdb.org/t/p/w342${result.posterUrl}",
                backgroundUrl = "https://image.tmdb.org/t/p/original${result.backgroundUrl}",
                releaseDate = result.releaseDate?.let { LocalDate.parse(it) },
                recommendations = result.recommendations?.results?.map { rec ->
                    convertMediaModel(rec)
                } ?: emptyList(),
                seasons = result.seasons?.map { season ->
                    convertSeasonModel(season)
                } ?: emptyList()
            )
        } else {
            Movie(
                id = result.id.toString(),
                imdbId = result.imdbId,
                title = result.title,
                description = result.description,
                tagline = result.tagline,
                posterUrl = "https://image.tmdb.org/t/p/w342${result.posterUrl}",
                backgroundUrl = "https://image.tmdb.org/t/p/original${result.backgroundUrl}",
                releaseDate = result.releaseDate?.let { LocalDate.parse(it) },
                recommendations = result.recommendations?.results?.map { rec ->
                    convertMediaModel(rec)
                } ?: emptyList()
            )
        }
    }

    private fun convertSeasonModel(result: Result): Season {
        return Season(
            id = result.id.toString(),
            title = result.title,
            seasonNumber = result.seasonNumber ?: 1,
            description = result.description,
            posterUrl = result.posterUrl,
            releaseDate = result.releaseDate?.let { LocalDate.parse(it) },
            episodes = result.episodes?.map { episode ->
                convertEpisodeModel(episode)
            } ?: emptyList()
        )
    }

    private fun convertEpisodeModel(result: Result): Episode {
        return Episode(
            id = result.id.toString(),
            imdbId = result.imdbId,
            episodeNumber = result.episodeNumber ?: 1,
            title = result.title,
            description = result.description,
            posterUrl = result.posterUrl,
            releaseDate = result.releaseDate?.let { LocalDate.parse(it) },
        )
    }
}