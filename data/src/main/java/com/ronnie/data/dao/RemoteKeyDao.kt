package com.ronnie.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ronnie.domain.RemoteKey

@Dao
interface RemoteKeyDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(remoteKey: List<RemoteKey>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrReplace(remoteKey: RemoteKey)

  @Query("SELECT * FROM remote_keys WHERE id = :id")
  suspend fun remoteKeysImageId(id: Int): RemoteKey?

  @Query("DELETE FROM remote_keys")
  suspend fun clearRemoteKeys()
}