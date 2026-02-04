package com.mavilon.mongomovies.data.remote.util

object ImageUrlBuilder {
    private const val BASE_URL = "https://image.tmdb.org/t/p/"
    
    enum class ImageSize(val path: String) {
        W342("w342"),
        W780("w780"),
        ORIGINAL("original")
    }
    
    fun buildImageUrl(path: String?, size: ImageSize = ImageSize.W342): String? {
        return path?.let { "$BASE_URL${size.path}$it" }
    }
    
    fun buildPosterUrl(path: String?): String? {
        return buildImageUrl(path, ImageSize.W342)
    }
    
    fun buildBackdropUrl(path: String?): String? {
        return buildImageUrl(path, ImageSize.W780)
    }
    
    fun buildProfileUrl(path: String?): String? {
        return buildImageUrl(path, ImageSize.W342)
    }
}
