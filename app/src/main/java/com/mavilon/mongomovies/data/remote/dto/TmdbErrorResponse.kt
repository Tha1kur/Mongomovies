package com.mavilon.mongomovies.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// TMDB Error Response
@Serializable
data class TmdbErrorResponse(
    @SerialName("status_code") val statusCode: Int,
    @SerialName("status_message") val statusMessage: String,
    @SerialName("success") val success: Boolean = false
)
