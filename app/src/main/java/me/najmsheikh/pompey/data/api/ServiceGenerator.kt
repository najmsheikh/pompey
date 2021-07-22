package me.najmsheikh.pompey.data.api

/**
 * A [retrofit2.Retrofit] service generator used to access some remote API.
 */
interface ServiceGenerator {

    /**
     * Retrieve the API service
     */
    val apiService: ApiService
}
