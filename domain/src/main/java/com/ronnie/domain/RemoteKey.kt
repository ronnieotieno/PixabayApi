package com.ronnie.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey val id: Int, val prevPage: Int?, val nextPage: Int?)