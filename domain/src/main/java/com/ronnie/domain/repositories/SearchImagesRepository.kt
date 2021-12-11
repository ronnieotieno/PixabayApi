package com.ronnie.domain.repositories

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.ronnie.domain.Image
import kotlinx.coroutines.flow.Flow

interface SearchImagesRepository {
    @ExperimentalPagingApi
    fun searchImages(searchString: String): Flow<PagingData<Image>>
}