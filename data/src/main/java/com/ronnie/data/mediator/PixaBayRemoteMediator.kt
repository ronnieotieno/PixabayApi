package com.ronnie.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.ronnie.commons.FIRST_PAGE
import com.ronnie.commons.PAGED_DATA_PER_PAGE
import com.ronnie.data.api.PixaBayApi
import com.ronnie.data.db.PixaBayRoomDb
import com.ronnie.data.mappers.toImageEntity
import com.ronnie.data.models.ImagesEntity
import com.ronnie.data.models.RemoteKey
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PixaBayRemoteMediator(
    private val query: String,
    private val pixaBayApi: PixaBayApi,
    private val pixaBayRoomDb: PixaBayRoomDb

) : RemoteMediator<Int, ImagesEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ImagesEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextPage?.minus(1) ?: FIRST_PAGE
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevPage
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextPage
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKey
            }
        }

        try {
            val imagesResponse = pixaBayApi.searchImages(
                searchString = query,
                page = page,
                per_page = PAGED_DATA_PER_PAGE
            )
            val images = imagesResponse.images
            val endOfPaginationReached = images.isEmpty()
            pixaBayRoomDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    pixaBayRoomDb.remoteKeyDao().clearRemoteKeys()
                    pixaBayRoomDb.imageDao().clearAll()
                }
                val prevPage = if (page == FIRST_PAGE) null else page - 1
                val nextPage = if (endOfPaginationReached) null else page + 1
                val keys = images.map {
                    RemoteKey(imageId = it.id, prevPage = prevPage, nextPage = nextPage)
                }
                pixaBayRoomDb.remoteKeyDao().insertAll(keys)
                pixaBayRoomDb.imageDao().insertAll(images.map { it.toImageEntity() })
            }
            return MediatorResult.Success(endOfPaginationReached = false)

        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ImagesEntity>): RemoteKey? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { repo ->
                pixaBayRoomDb.remoteKeyDao().remoteKeysImageId(repo.id)
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ImagesEntity>): RemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { repo ->
                pixaBayRoomDb.remoteKeyDao().remoteKeysImageId(repo.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, ImagesEntity>
    ): RemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                pixaBayRoomDb.remoteKeyDao().remoteKeysImageId(repoId)
            }
        }
    }
}