package me.najmsheikh.pompey.data.models

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MediaContentSource(
    val title: String,
    val source: Uri,
) : Parcelable
