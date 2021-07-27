package me.najmsheikh.pompey.data.api.sourcerer

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("streams") val results: List<StreamResult>,
)

data class StreamResult(
    @SerializedName("name", alternate = ["title"]) val title: String,
    @SerializedName("url", alternate = ["externalUrl"]) val url: String,
)
