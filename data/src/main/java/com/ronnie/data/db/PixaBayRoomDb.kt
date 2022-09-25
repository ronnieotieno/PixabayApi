package com.ronnie.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ronnie.data.dao.ImageDao
import com.ronnie.data.dao.RemoteKeyDao
import com.ronnie.data.models.entities.ImagesEntity
import com.ronnie.data.models.entities.RemoteKey

/**
 * Room, architecture component build on top of Sqlite.
 * Used to cache images.
 */

@Database(
    entities = [ImagesEntity::class, RemoteKey::class],
    version = 1, exportSchema = false
)
abstract class PixaBayRoomDb : RoomDatabase() {
    abstract fun imageDao(): ImageDao
    abstract fun remoteKeyDao(): RemoteKeyDao

}