package me.najmsheikh.pompey.data.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubtitleSource(
    val language: String,
    val source: Uri,
) : Parcelable
