package me.najmsheikh.pompey.data.models

import java.time.LocalDate

/**
 * Created by Najm Sheikh <hello@najmsheikh.me> on 7/21/21.
 */
data class Video(
    val id: String,
    val title: String,
    val description: String,
    val posterUrl: String?,
    val backgroundUrl: String?,
    val releaseDate: LocalDate?,
)
