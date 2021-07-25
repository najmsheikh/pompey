package me.najmsheikh.pompey.data.models

import android.os.Parcelable
import java.time.LocalDate

/**
 * Meta-data regarding a media content.
 */
sealed interface MediaContent : Parcelable {
    val id: String
    val imdbId: String?
    val title: String
    val description: String
    val tagline: String?
    val posterUrl: String?
    val backgroundUrl: String?
    val releaseDate: LocalDate?
    val recommendations: List<MediaContent>
}
