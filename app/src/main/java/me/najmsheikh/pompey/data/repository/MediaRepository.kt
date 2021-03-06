package me.najmsheikh.pompey.data.repository

import me.najmsheikh.pompey.data.api.tmdb.Result
import me.najmsheikh.pompey.data.api.tmdb.TmdbApiService
import me.najmsheikh.pompey.data.models.MediaContent
import me.najmsheikh.pompey.data.models.Movie
import me.najmsheikh.pompey.data.models.Show
import me.najmsheikh.pompey.data.models.Show.Season
import me.najmsheikh.pompey.data.models.Show.Season.Episode
import me.najmsheikh.pompey.parseAsDate

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

    suspend fun searchForContent(searchQuery: String, pageNumber: Int = 1): List<MediaContent> {
        val response = tmdbApiService.searchMultiple(
            searchQuery = searchQuery,
            pageNumber = pageNumber,
        )

        return response.results
            .filter { result -> result.mediaType == "tv" || result.mediaType == "movie" }
            .map(this::convertMediaModel)
    }

    suspend fun getMovieDetails(id: String): Movie {
        val response = tmdbApiService.getMovieDetails(id)

        return convertMediaModel(response) as Movie
    }

    suspend fun getShowDetails(id: String): Show {
        val showResponse = tmdbApiService.getShowDetails(id)

        return convertMediaModel(showResponse) as Show
    }

    suspend fun getSeasonDetails(showId: String, showImdbId: String?, seasonNumber: Int): Season {
        val result = tmdbApiService.getShowSeasonDetails(showId, seasonNumber)

        return convertSeasonModel(showId, showImdbId, result)
    }

    suspend fun getEpisodeDetails(
        showId: String,
        showImdbId: String?,
        seasonNumber: Int,
        episodeNumber: Int,
    ): Episode {
        val result = tmdbApiService.getEpisodeDetails(showId, seasonNumber, episodeNumber)

        return convertEpisodeModel(showId, showImdbId, seasonNumber, result)
    }

    private fun convertMediaModel(result: Result): MediaContent {
        return if (result.mediaType == "tv" || !result.seasons.isNullOrEmpty()) {
            Show(
                id = result.id.toString(),
                imdbId = result.ids?.imdbId,
                title = result.title,
                description = result.description,
                tagline = result.tagline,
                posterUrl = "https://image.tmdb.org/t/p/w342${result.posterUrl}",
                backgroundUrl = "https://image.tmdb.org/t/p/original${result.backgroundUrl}",
                releaseDate = result.releaseDate.parseAsDate(),
                recommendations = result.recommendations?.results?.map { rec ->
                    convertMediaModel(rec)
                } ?: emptyList(),
                seasons = result.seasons?.map { season ->
                    convertSeasonModel(result.id.toString(), result.ids?.imdbId, season)
                } ?: emptyList()
            )
        } else {
            Movie(
                id = result.id.toString(),
                imdbId = result.ids?.imdbId,
                title = result.title,
                description = result.description,
                tagline = result.tagline,
                posterUrl = "https://image.tmdb.org/t/p/w342${result.posterUrl}",
                backgroundUrl = "https://image.tmdb.org/t/p/original${result.backgroundUrl}",
                releaseDate = result.releaseDate.parseAsDate(),
                recommendations = result.recommendations?.results?.map { rec ->
                    convertMediaModel(rec)
                } ?: emptyList()
            )
        }
    }

    private fun convertSeasonModel(showId: String, showImdbId: String?, result: Result): Season {
        return Season(
            id = result.id.toString(),
            showId = showId,
            showImdbId = showImdbId,
            title = result.title,
            seasonNumber = result.seasonNumber ?: 1,
            description = result.description,
            posterUrl = "https://image.tmdb.org/t/p/w342${result.posterUrl}",
            releaseDate = result.releaseDate.parseAsDate(),
            episodes = result.episodes?.map { episode ->
                convertEpisodeModel(showId, showImdbId, result.seasonNumber ?: 1, episode)
            } ?: emptyList()
        )
    }

    private fun convertEpisodeModel(
        showId: String,
        showImdbId: String?,
        seasonNumber: Int,
        result: Result,
    ): Episode {
        return Episode(
            id = result.id.toString(),
            imdbId = result.ids?.imdbId,
            showId = showId,
            showImdbId = showImdbId,
            seasonNumber = seasonNumber,
            episodeNumber = result.episodeNumber ?: 1,
            title = result.title,
            description = result.description,
            posterUrl = "https://image.tmdb.org/t/p/original${result.posterUrl}",
            releaseDate = result.releaseDate.parseAsDate(),
        )
    }
}