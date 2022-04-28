package com.ronnie.domain.usecases

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.ronnie.domain.models.Image
import com.ronnie.domain.repositories.SearchImagesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchUseCase @Inject constructor(private val searchImagesRepository: SearchImagesRepository) {

    @OptIn(ExperimentalPagingApi::class)
    suspend fun invoke(payload: String): Flow<PagingData<Image>> {
        return searchImagesRepository.searchImages(payload)
    }

}