package com.ronnie.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ronnie.commons.DB_NAME
import com.ronnie.data.dao.ImageDao
import com.ronnie.data.dao.RemoteKeyDao
import com.ronnie.data.models.ImagesEntity
import com.ronnie.domain.models.Image
import com.ronnie.domain.models.RemoteKey
import javax.inject.Singleton

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