package com.ronnie.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.ronnie.data.api.PixaBayApi
import com.ronnie.data.db.PixaBayRoomDb
import com.ronnie.domain.Image
import com.ronnie.domain.RemoteKey
import retrofit2.HttpException
import java.io.IOException
import java.io.InvalidObjectException

@ExperimentalPagingApi
class PixaRemoteMediator(
    private val pixaBayApi: PixaBayApi,
    private val searchString: String,
    private val db: PixaBayRoomDb
) : RemoteMediator<Int, Image>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Image>): MediatorResult {
        val page: Int = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextPage?.minus(1) ?: 1
            }

            LoadType.PREPEND -> {
                return MediatorResult.Success(true)
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                    ?: throw InvalidObjectException("Result is empty")
                remoteKeys.nextPage ?: return MediatorResult.Success(true)
            }
        }

        try {
            val response = pixaBayApi.searchImages(searchString,state.config.initialLoadSize, page)
            val images = response.images

            images.map {
                it.searchTerm = searchString
            }

            val endOfPaginationReached = images.isEmpty()

                if (loadType == LoadType.REFRESH) {
                    db.withTransaction {
                        db.remoteKeyDao().clearRemoteKeys()
                        db.imageDao().clearAll()
                    }
                }
               val prevKey = if (page == 1) null else page - 1
               val  nextKey = if (endOfPaginationReached) null else page + 1

               val keys = images.map {
                    RemoteKey(id= it.id, prevPage = prevKey, nextPage = nextKey)
                }
            db.withTransaction {
                db.remoteKeyDao().insertAll(keys)
                db.imageDao().insertAll(images)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }

    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Image>): RemoteKey? {
        return state.lastItemOrNull()?.let { image ->
                db.withTransaction {
                    db.remoteKeyDao().remoteKeysImageId(image.id)
                }
            }
    }
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Image>): RemoteKey? {
        return state.firstItemOrNull()?.let { image ->
                db.withTransaction {
                    db.remoteKeyDao().remoteKeysImageId(image.id)
                }
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Image>
    ): RemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.let { image ->
                db.withTransaction {
                    db.remoteKeyDao().remoteKeysImageId(image.id)
                }
            }
        }
    }
}