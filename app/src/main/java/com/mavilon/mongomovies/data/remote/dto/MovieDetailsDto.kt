package com.mavilon.mongomovies.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetailsDto(
    @SerialName("id") val id: Int,
    @SerialName("title") val title: String,
    @SerialName("poster_path") val posterPath: String? = null,
    @SerialName("backdrop_path") val backdropPath: String? = null,
    @SerialName("overview") val overview: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0,
    @SerialName("runtime") val runtime: Int? = null,
    @SerialName("genres") val genres: List<GenreDto> = emptyList(),
    @SerialName("tagline") val tagline: String? = null,
    @SerialName("status") val status: String? = null
)

@Serializable
data class GenreDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String
)
