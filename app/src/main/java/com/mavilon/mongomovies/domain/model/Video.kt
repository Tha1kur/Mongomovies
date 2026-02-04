package com.mavilon.mongomovies.domain.model

data class Video(
    val id: String,
    val name: String,
    val site: String,
    val key: String?,
    val type: String?,
    val official: Boolean
)
