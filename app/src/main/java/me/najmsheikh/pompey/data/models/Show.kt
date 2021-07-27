package me.najmsheikh.pompey.data.models

import kotlinx.parcelize.Parcelize
import java.time.LocalDate

/**
 * Created by Najm Sheikh <hello@najmsheikh.me> on 7/23/21.
 */
@Parcelize
data class Show(
    override val id: String,
    override val imdbId: String?,
    override val title: String,
    override val description: String,
    override val tagline: String?,
    override val posterUrl: String?,
    override val backgroundUrl: String?,
    override val releaseDate: LocalDate?,
    override val recommendations: List<MediaContent>,
    val seasons: List<Season>,
) : MediaContent {

    @Parcelize
    data class Season(
        override val id: String,
        override val imdbId: String? = null,
        val showId: String,
        val showImdbId: String?,
        val seasonNumber: Int,
        override val title: String,
        override val description: String,
        override val posterUrl: String?,
        override val releaseDate: LocalDate?,
        val episodes: List<Episode>,
        override val tagline: String? = null,
        override val backgroundUrl: String? = null,
        override val recommendations: List<MediaContent> = emptyList(),
    ) : MediaContent {

        @Parcelize
        data class Episode(
            override val id: String,
            override val imdbId: String?,
            val showId: String,
            val showImdbId: String?,
            val seasonNumber: Int,
            val episodeNumber: Int,
            override val title: String,
            override val description: String,
            override val posterUrl: String?,
            override val releaseDate: LocalDate?,
            override val tagline: String? = null,
            override val backgroundUrl: String? = null,
            override val recommendations: List<MediaContent> = emptyList(),
        ) : MediaContent
    }
}
