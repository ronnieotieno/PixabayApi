package com.ronnie.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ronnie.commons.PAGE_SIZE
import com.ronnie.data.api.PixaBayApi
import com.ronnie.data.datasource.PixaDataSource
import com.ronnie.data.db.PixaBayRoomDb
import com.ronnie.domain.models.Image
import com.ronnie.domain.repositories.SearchImagesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class SearchImagesRepositoryImpl @Inject constructor(
    private val pixaBayApi: PixaBayApi,
    private val pixaBayRoomDb: PixaBayRoomDb,
) : SearchImagesRepository {

    override fun searchImages(searchString: String): Flow<PagingData<Image>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = PAGE_SIZE),
            pagingSourceFactory = {
                PixaDataSource(searchString, pixaBayApi)
            }
        ).flow
    }
    /**
     * Caching data with Remote mediator had issues and I wasn't able to solve, I resorted
     * to using okhttp cache instead
     */
//    @OptIn(ExperimentalPagingApi::class)
//    override fun searchImages(searchString: String): Flow<PagingData<Image>> {
//        val dbQuery = "%${searchString.replace(' ', '%')}%"
//        return Pager(
//            config = PagingConfig(
//                pageSize = PAGE_SIZE,
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