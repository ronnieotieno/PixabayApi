package com.ronnie.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey
    val imageId: Int,
    val prevPage: Int?,
    val nextPage: Int?
)