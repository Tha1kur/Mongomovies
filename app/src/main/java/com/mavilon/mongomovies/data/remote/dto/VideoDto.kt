package com.mavilon.mongomovies.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("site") val site: String,
    @SerialName("key") val key: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("official") val official: Boolean = false
)

@Serializable
data class VideosResponse(
    @SerialName("id") val id: Int,
    @SerialName("results") val results: List<VideoDto>
)
