package com.mavilon.mongomovies.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CastDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("character") val character: String? = null,
    @SerialName("profile_path") val profilePath: String? = null,
    @SerialName("order") val order: Int = 0
)

@Serializable
data class CreditsResponse(
    @SerialName("id") val id: Int,
    @SerialName("cast") val cast: List<CastDto> = emptyList()
)
