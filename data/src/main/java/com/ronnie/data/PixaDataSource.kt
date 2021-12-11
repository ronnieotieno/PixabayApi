package com.ronnie.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ronnie.data.api.PixaBayApi
import com.ronnie.domain.Image
import java.io.IOException
//
//class PixaDataSource(private val searchString: String, private val pixaBayApi: PixaBayApi) :
//    PagingSource<Int, Image>() {
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Image> {
//        val page = params.key ?: 1
//        return try {
//            val data = pixaBayApi.searchImages(searchString, params.loadSize, page)
//
//            LoadResult.Page(
//                data = data.images,
//                prevKey = if (page == 1) null else page - 1,
//                nextKey = if (data.images.isEmpty()) null else page + 1
//            )
//        } catch (t: Throwable) {
//            var exception = t
//            if (t is IOException) {
//                exception = IOException("Please check internet connection")
//            }

//            LoadResult.Error(exception)
//        }
//
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, Image>): Int? {
//        return state.anchorPosition
//    }
//}