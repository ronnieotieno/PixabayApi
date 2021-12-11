package com.ronnie.domain.usecases

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import com.ronnie.domain.Image
import com.ronnie.domain.repositories.SearchImagesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchUseCase @Inject constructor(private val searchImagesRepository: SearchImagesRepository) :
    BaseUseCase<String, Flow<PagingData<Image>>> {
    @ExperimentalPagingApi
    override suspend fun invoke(payload: String):  Flow<PagingData<Image>>{
        return searchImagesRepository.searchImages(payload)
    }
}