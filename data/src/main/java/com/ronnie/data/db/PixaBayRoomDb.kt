package com.ronnie.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ronnie.commons.DB_NAME
import com.ronnie.data.dao.ImageDao
import com.ronnie.data.dao.RemoteKeyDao
import com.ronnie.domain.models.Image
import com.ronnie.domain.models.RemoteKey
import javax.inject.Singleton


@Database(
    entities = [Image::class, RemoteKey::class],
    version = 4, exportSchema = false
)
@Singleton
abstract class PixaBayRoomDb : RoomDatabase() {
    abstract fun imageDao(): ImageDao
    abstract fun remoteKeyDao():RemoteKeyDao

    companion object {
        @Volatile
        private var instance: PixaBayRoomDb? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance
            ?: synchronized(LOCK) {
                instance
                    ?: buildDatabase(
                        context
                    ).also {
                        instance = it
                    }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                PixaBayRoomDb::class.java,
                DB_NAME
            ).fallbackToDestructiveMigration()
                .build()
    }

}