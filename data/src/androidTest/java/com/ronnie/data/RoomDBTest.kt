package com.ronnie.data

import android.content.Context
import androidx.room.Room
import androidx.room.withTransaction
import androidx.test.platform.app.InstrumentationRegistry
import com.google.gson.Gson
import com.ronnie.data.dao.ImageDao
import com.ronnie.data.dao.RemoteKeyDao
import com.ronnie.data.db.PixaBayRoomDb
import com.ronnie.domain.models.ImageResponse
import com.ronnie.domain.models.RemoteKey
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.InputStream
import kotlin.random.Random


class RoomDBTest {

    private lateinit var db: PixaBayRoomDb
    private lateinit var context: Context
    private lateinit var remoteKeyDao: RemoteKeyDao
    private lateinit var imageDao: ImageDao
    private val gson = Gson()

    @Before
    fun setUp(){

        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, PixaBayRoomDb::class.java).build()
        val jsonStream: InputStream = context.resources.assets.open("response.json")
        val jsonBytes: ByteArray = jsonStream.readBytes()

        val images = gson.fromJson(String(jsonBytes), ImageResponse::class.java).images

        images.map {
            it.searchTerm = "fruits"
        }

        imageDao = db.imageDao()
        remoteKeyDao = db.remoteKeyDao()

        val keys =  images.map {
            RemoteKey(0,it.id,1,2)
        }

        runBlocking {
            db.withTransaction {
              imageDao.insertAll(images)
              remoteKeyDao.insertAll(keys)
            }
        }
    }

    @After
    fun clear(){
        db.clearAllTables()
        db.close()
    }

    @Test
    fun return_true_number_keys_equal_images() = runBlocking{
        assertThat(
            imageDao.getAll().size,CoreMatchers.equalTo(
                remoteKeyDao.getAll().size))
    }

    @Test
    fun return_true_if_search_term_is_fruits() = runBlocking{
       val result =  imageDao.getAll()[Random(0).nextInt(19)].searchTerm == "fruits"
        assertThat(result, CoreMatchers.equalTo(true))
    }
}