package com.ronnie.presenatation.model

data class ImagePresentation(
    val comments: Int,
    val downloads: Int,
    val id: Int,
    val largeImageURL: String,
    val likes: Int,
    val tags: String,
    val user: String,
    val user_id: Int,
    val views: Int,
    var searchTerm: String? = null
)
