package com.mavilon.mongomovies.domain.model

data class MovieDetails(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val overview: String?,
    val releaseDate: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val runtime: Int?,
    val genres: List<Genre>,
    val tagline: String?,
    val status: String?
)

data class Genre(
    val id: Int,
    val name: String
)
