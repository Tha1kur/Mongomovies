package com.mavilon.mongomovies.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val overview: String?,
    val releaseDate: String?,
    val voteAverage: Double,
    val voteCount: Int,
    val genreIds: List<Int>
)
