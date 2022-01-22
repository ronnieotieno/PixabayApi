package com.ronnie.data.datasource

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ronnie.commons.FIRST_PAGE
import com.ronnie.data.R
import com.ronnie.data.api.PixaBayApi
import com.ronnie.domain.models.Image
import java.io.IOException

/**
 * Paging 3 data source to query the pages based on recyclerview scroll state
 */
class PixaDataSource(
    private val searchString: String,
    private val pixaBayApi: PixaBayApi,
    private val context: Context
) :
    PagingSource<Int, Image>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Image> {
        val page = params.key ?: FIRST_PAGE
        return try {
            val data = pixaBayApi.searchImages(searchString, params.loadSize, page)

            LoadResult.Page(
                data = data.images,
                prevKey = if (page == FIRST_PAGE) null else page - 1,
                nextKey = if (data.images.isEmpty()) null else page + 1
            )
        } catch (t: Throwable) {
            var exception = t
            if (t is IOException) {
                exception = IOException(context.getString(R.string.network_error))
            }

            LoadResult.Error(exception)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, Image>): Int? {
        return state.anchorPosition
    }
}