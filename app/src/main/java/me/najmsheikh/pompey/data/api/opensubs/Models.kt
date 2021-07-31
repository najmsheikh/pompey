package me.najmsheikh.pompey.data.api.opensubs

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("result") val resultList: ResultList,
) {

    data class ResultList(
        @SerializedName("all") val allResults: List<Result>,
    )
}

data class Result(
    @SerializedName("lang") val language: String,
    @SerializedName("SubEncoding") val encoding: String,
    @SerializedName("url") val url: String,
)
