package com.ronnie.data.repository

import android.util.Log
import androidx.paging.*
import com.ronnie.data.api.PixaBayApi
import com.ronnie.data.db.PixaBayRoomDb
import com.ronnie.data.mappers.toDomainImage
import com.ronnie.data.mediator.PixaBayRemoteMediator
import com.ronnie.domain.models.Image
import com.ronnie.domain.repositories.SearchImagesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class SearchImagesRepositoryImpl @Inject constructor(
    private val pixaBayApi: PixaBayApi,
    private val pixaBayRoomDb: PixaBayRoomDb,
) : SearchImagesRepository {
    @ExperimentalPagingApi
    override fun searchImages(searchString: String): Flow<PagingData<Image>> {
        val pagingSourceFactory = { pixaBayRoomDb.imageDao().queryImages(searchString) }
        Log.d("Repository", "searchImages: $searchString")
        return Pager(
            config = PagingConfig(
                pageSize = NETWORK_PAGE_SIZE,
                maxSize = NETWORK_PAGE_SIZE + (NETWORK_PAGE_SIZE * 2),
                enablePlaceholders = false
            ),
            remoteMediator = PixaBayRemoteMediator(
                searchString,
                pixaBayApi,
                pixaBayRoomDb
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { imageEntity ->
                imageEntity.toDomainImage()
            }
        }
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }

}