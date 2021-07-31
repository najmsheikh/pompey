package me.najmsheikh.pompey.data.repository

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import me.najmsheikh.pompey.data.api.opensubs.OpenSubtitlesApiService
import me.najmsheikh.pompey.data.api.sourcerer.SourcererApiService
import me.najmsheikh.pompey.data.models.MediaContentSource
import me.najmsheikh.pompey.data.models.SubtitleSource
import me.najmsheikh.pompey.encodeToBase64

class SourceRepository(
    private val sourcererApiService: SourcererApiService,
    private val openSubtitlesApiService: OpenSubtitlesApiService,
) {

    suspend fun getMovieSources(
        imdbId: String,
    ): List<MediaContentSource> = withContext(Dispatchers.IO) {
        val subtitleReq = SUBTITLE_REQUEST_BODY.format(imdbId)
        val encodedRequest = subtitleReq.encodeToBase64()

        val subtitleRes = async { openSubtitlesApiService.getSubtitles(encodedRequest) }
        val sourceRes = async { sourcererApiService.getMovieSources(imdbId) }

        return@withContext sourceRes.await().results.map { result ->
            MediaContentSource(
                title = result.title,
                source = Uri.parse(result.url),
                subtitles = subtitleRes.await().resultList.allResults.map { subResult ->
                    SubtitleSource(
                        language = subResult.language,
                        source = Uri.parse(subResult.url)
                    )
                }
            )
        }
    }

    suspend fun getEpisodeSources(
        showImdbId: String,
        seasonNumber: Int,
        episodeNumber: Int,
    ): List<MediaContentSource> = withContext(Dispatchers.IO) {
        val subtitleReq = SUBTITLE_REQUEST_BODY.format("$showImdbId $seasonNumber $episodeNumber")
        val encodedRequest = subtitleReq.encodeToBase64()

        val subtitleRes = async { openSubtitlesApiService.getSubtitles(encodedRequest) }
        val sourceRes = async {
            sourcererApiService.getShowSources(showImdbId, seasonNumber, episodeNumber)
        }

        return@withContext sourceRes.await().results.map { result ->
            MediaContentSource(
                title = result.title,
                source = Uri.parse(result.url),
                subtitles = subtitleRes.await().resultList.allResults.map { subResult ->
                    SubtitleSource(
                        language = subResult.language,
                        source = Uri.parse(subResult.url)
                    )
                }
            )
        }
    }

    companion object {

        private const val SUBTITLE_REQUEST_BODY =
            """
            {
                "params": [
                    null,
                    {
                        "query": {
                            "itemHash":"%s"
                        }
                    }
                ],
                "method": "subtitles.find",
                "id": 1, 
                "jsonrpc": "2.0"
            }
            """

    }

}
