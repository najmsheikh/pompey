package me.najmsheikh.pompey.data.models

import kotlinx.parcelize.Parcelize
import java.time.LocalDate

/**
 * Created by Najm Sheikh <hello@najmsheikh.me> on 7/22/21.
 */
@Parcelize
data class Movie(
    override val id: String,
    override val imdbId: String?,
    override val title: String,
    override val description: String,
    override val tagline: String?,
    override val posterUrl: String?,
    override val backgroundUrl: String?,
    override val releaseDate: LocalDate?,
    override val recommendations: List<MediaContent>,
) : MediaContent
