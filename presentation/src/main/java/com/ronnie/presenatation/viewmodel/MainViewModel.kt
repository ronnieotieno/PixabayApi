package com.ronnie.presenatation.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.ronnie.commons.DEFAULT_SEARCH
import com.ronnie.domain.models.Image
import com.ronnie.domain.usecases.SearchUseCase
import com.ronnie.presenatation.mappers.toImagePresentation
import com.ronnie.presenatation.model.ImagePresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
) : ViewModel() {


    var selectedImage: ImagePresentation? = null
    private val defaultSearch = DEFAULT_SEARCH
    var currentSearch = defaultSearch


//    fun searchImages(searchString: String): Flow<PagingData<ImagePresentation>> {
//        currentSearch = searchString
//        return searchUseCase.invoke(searchString)
//    }

    fun searchImages(searchString: String): Flow<PagingData<ImagePresentation>> {
        currentSearch = searchString
        return searchUseCase(searchString).map { paginatedData ->
            paginatedData.map { image ->
                image.toImagePresentation()
            }
            Log.d("ViewModel", "searchImages: $paginatedData")
            paginatedData.map { it.toImagePresentation() }

        }
    }

}