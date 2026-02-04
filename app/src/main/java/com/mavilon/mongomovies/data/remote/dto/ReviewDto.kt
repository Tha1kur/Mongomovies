package com.mavilon.mongomovies.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReviewDto(
    @SerialName("id") val id: String,
    @SerialName("author") val author: String,
    @SerialName("content") val content: String,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("author_details") val authorDetails: AuthorDetailsDto? = null
)

@Serializable
data class AuthorDetailsDto(
    @SerialName("name") val name: String? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("avatar_path") val avatarPath: String? = null,
    @SerialName("rating") val rating: Double? = null
)

@Serializable
data class ReviewsResponse(
    @SerialName("id") val id: Int,
    @SerialName("page") val page: Int,
    @SerialName("results") val results: List<ReviewDto> = emptyList(),
    @SerialName("total_pages") val totalPages: Int,
    @SerialName("total_results") val totalResults: Int
)
