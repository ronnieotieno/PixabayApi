package com.ronnie.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ronnie.data.PixaRemoteMediator
import com.ronnie.data.api.PixaBayApi
import com.ronnie.data.db.PixaBayRoomDb
import com.ronnie.domain.Image
import com.ronnie.domain.repositories.SearchImagesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchImagesRepositoryImpl @Inject constructor(private val pixaBayApi: PixaBayApi, private val pixaBayRoomDb: PixaBayRoomDb):SearchImagesRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun searchImages(searchString: String): Flow<PagingData<Image>> {
        val dbQuery = "%${searchString.replace(' ', '%')}%"
        val pagingSourceFactory = { pixaBayRoomDb.imageDao().queryImages(dbQuery) }
            return Pager(
                config = PagingConfig(
                    pageSize = 25,
                    prefetchDistance = 10 ,
                    initialLoadSize = 25,
                    enablePlaceholders = false
                ),
                remoteMediator = PixaRemoteMediator(
                    pixaBayApi,
                    searchString,
                    pixaBayRoomDb
                ),
                pagingSourceFactory = pagingSourceFactory
            ).flow
        }

}