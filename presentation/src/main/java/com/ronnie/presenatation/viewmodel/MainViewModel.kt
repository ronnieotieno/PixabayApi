package com.ronnie.presenatation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.ronnie.commons.DEFAULT_SEARCH
import com.ronnie.domain.usecases.SearchUseCase
import com.ronnie.presenatation.mappers.toImagePresentation
import com.ronnie.presenatation.model.ImagePresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
) : ViewModel() {


    var selectedImage: ImagePresentation? = null
    private val defaultSearch = DEFAULT_SEARCH
    var currentSearch = defaultSearch

    fun searchImages(searchString: String): Flow<PagingData<ImagePresentation>> {
        currentSearch = searchString
        return searchUseCase(searchString).map { paginatedData ->
            paginatedData.map { image ->
                image.toImagePresentation()
            }
            paginatedData.map { data ->
                data.toImagePresentation()
            }

        }.cachedIn(viewModelScope)
    }

}