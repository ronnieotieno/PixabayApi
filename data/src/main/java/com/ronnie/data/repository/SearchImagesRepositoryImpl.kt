package com.ronnie.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ronnie.data.PixaDataSource
import com.ronnie.data.PixaRemoteMediator
import com.ronnie.data.api.PixaBayApi
import com.ronnie.data.db.PixaBayRoomDb
import com.ronnie.domain.Image
import com.ronnie.domain.repositories.SearchImagesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class SearchImagesRepositoryImpl @Inject constructor(private val pixaBayApi: PixaBayApi, private val pixaBayRoomDb: PixaBayRoomDb):SearchImagesRepository {

    override fun searchImages(searchString: String): Flow<PagingData<Image>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 25),
            pagingSourceFactory = {
                PixaDataSource(searchString,pixaBayApi)
            }
        ).flow
    }
//    @OptIn(ExperimentalPagingApi::class)
//    override fun searchImages(searchString: String): Flow<PagingData<Image>> {
//        val dbQuery = "%${searchString.replace(' ', '%')}%"
//        return Pager(
//            config = PagingConfig(
//                pageSize = 25,
//                enablePlaceholders = false
//            ),
//            remoteMediator = PixaRemoteMediator(
//                pixaBayApi,
//                searchString,
//                pixaBayRoomDb
//            )
//        ) { pixaBayRoomDb.imageDao().queryImages(dbQuery)}.flow
//    }
}