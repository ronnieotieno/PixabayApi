package com.ronnie.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.ronnie.commons.DEFAULT_SEARCH
import com.ronnie.commons.FIRST_PAGE
import com.ronnie.commons.PAGED_DATA_PER_PAGE
import com.ronnie.data.api.PixaBayApi
import com.ronnie.data.db.PixaBayRoomDb
import com.ronnie.data.mappers.toImageEntity
import com.ronnie.data.models.entities.ImagesEntity
import com.ronnie.data.models.entities.RemoteKey
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PixaBayRemoteMediator(
    private val query: String,
    private val pixaBayApi: PixaBayApi,
    private val pixaBayRoomDb: PixaBayRoomDb

) : RemoteMediator<Int, ImagesEntity>() {

    //when uncommented this function will prevent the remote mediator from refreshing in every intial launch of the app
    //though this brings a bug such that the on launch the app will do things at once one,fetch data from room and secondly perform a network request so as to update
    //data in room.When one is not connected to the internet,the error view and the loaded data from room will overlay each other
    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }


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
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextPage
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
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
                pixaBayRoomDb.imageDao()
                    .insertAll(images.map { it.toImageEntity(searchSting = query) })
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