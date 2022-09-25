package com.ronnie.data.datasource

/**
 * Paging 3 data source to query the pages based on recyclerview scroll state
 */
//class PixaDataSource(
//    private val searchString: String,
//    private val pixaBayApi: PixaBayApi,
//) :
//    PagingSource<Int, Image>() {
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Image> {
//        val page = params.key ?: FIRST_PAGE
//        return try {
//            val data = pixaBayApi.searchImages(searchString, params.loadSize, page)
//
//            LoadResult.Page(
//                data = data.images,
//                prevKey = if (page == FIRST_PAGE) null else page - 1,
//                nextKey = if (data.images.isEmpty()) null else page + 1
//            )
//        } catch (t: Throwable) {
//            var exception = t
//            if (t is IOException) {
//                exception = IOException("Please check your internet connection and try again")
//            }
//
//            LoadResult.Error(exception)
//        }
//
//    }
//
//    override fun getRefreshKey(state: PagingState<Int, Image>): Int? {
//        return state.anchorPosition
//    }
//}