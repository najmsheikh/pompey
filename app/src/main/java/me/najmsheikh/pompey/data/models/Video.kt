package me.najmsheikh.pompey.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

/**
 * Meta-data regarding a content video.
 */
@Parcelize
data class Video(
    val id: String,
    val title: String,
    val description: String,
    val posterUrl: String?,
    val backgroundUrl: String?,
    val releaseDate: LocalDate?,
) : Parcelable
