package com.mavilon.mongomovies.util

object ImageUrlBuilder {
    private const val BASE_URL = "https://image.tmdb.org/t/p/"

    fun getPosterUrl(path: String?): String? {
        return path?.let { "$BASE_URL/w500$it" }
    }

    fun getBackdropUrl(path: String?): String? {
        return path?.let { "$BASE_URL/w780$it" }
    }

    fun getCastUrl(path: String?): String? {
        return path?.let { "$BASE_URL/w185$it" }
    }
}
