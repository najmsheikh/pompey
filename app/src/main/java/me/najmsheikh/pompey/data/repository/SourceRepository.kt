package me.najmsheikh.pompey.data.repository

import android.net.Uri
import me.najmsheikh.pompey.data.api.sourcerer.SourcererApiService
import me.najmsheikh.pompey.data.models.MediaContentSource

class SourceRepository(private val sourcererApiService: SourcererApiService) {

    suspend fun getMovieSources(imdbId: String): List<MediaContentSource> {
        val response = sourcererApiService.getMovieSources(imdbId)

        return response.results.map { result ->
            MediaContentSource(
                title = result.title,
                source = Uri.parse(result.url)
            )
        }
    }

    suspend fun getEpisodeSources(
        showImdbId: String,
        seasonNumber: Int,
        episodeNumber: Int,
    ): List<MediaContentSource> {
        val response = sourcererApiService.getShowSources(showImdbId, seasonNumber, episodeNumber)

        return response.results.map { result ->
            MediaContentSource(
                title = result.title,
                source = Uri.parse(result.url)
            )
        }
    }

}
