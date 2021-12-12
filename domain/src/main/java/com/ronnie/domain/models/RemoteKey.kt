package com.ronnie.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, val imageId: Int, val prevPage: Int?, val nextPage: Int?)