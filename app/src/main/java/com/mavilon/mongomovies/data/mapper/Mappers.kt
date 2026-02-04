package com.mavilon.mongomovies.data.mapper

import com.mavilon.mongomovies.data.remote.dto.*
import com.mavilon.mongomovies.domain.model.*

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        posterPath = posterPath,
        backdropPath = backdropPath,
        overview = overview,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        genreIds = genreIds
    )
}

fun MovieDetailsDto.toDomain(): MovieDetails {
    return MovieDetails(
        id = id,
        title = title,
        posterPath = posterPath,
        backdropPath = backdropPath,
        overview = overview,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        runtime = runtime,
        genres = genres.map { it.toDomain() },
        tagline = tagline,
        status = status
    )
}

fun GenreDto.toDomain(): Genre {
    return Genre(
        id = id,
        name = name
    )
}

fun CastDto.toDomain(): Cast {
    return Cast(
        id = id,
        name = name,
        character = character,
        profilePath = profilePath,
        order = order
    )
}

fun ReviewDto.toDomain(): Review {
    return Review(
        id = id,
        author = author,
        content = content,
        createdAt = createdAt,
        rating = authorDetails?.rating
    )
}
