package com.ronnie.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "image_table")
data class Image(
    val comments: Int,
    val downloads: Int,
    @PrimaryKey val id: Int,
    val largeImageURL: String,
    val likes: Int,
    val tags: String,
    val user: String,
    val user_id: Int,
    val views: Int,
    var searchTerm:String? = null
)