package me.najmsheikh.pompey.data.models

import android.os.Parcelable
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
        val id: String,
        val seasonNumber: Int,
        val title: String,
        val description: String,
        val posterUrl: String?,
        val releaseDate: LocalDate?,
        val episodes: List<Episode>,
    ) : Parcelable {

        @Parcelize
        data class Episode(
            val id: String,
            val imdbId: String?,
            val episodeNumber: Int,
            val title: String,
            val description: String,
            val posterUrl: String?,
            val releaseDate: LocalDate?,
        ) : Parcelable
    }
}
