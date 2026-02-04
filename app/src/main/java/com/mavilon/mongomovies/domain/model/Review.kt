package com.mavilon.mongomovies.domain.model

data class Review(
    val id: String,
    val author: String,
    val content: String,
    val createdAt: String?,
    val rating: Double?
)
