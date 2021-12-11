package com.ronnie.presenatation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ronnie.domain.Image
import com.ronnie.domain.usecases.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase):ViewModel() {

    var selectedImage: Image? = null
    var currentSearch = "fruits"

    private var currentSearchResult: Flow<PagingData<Image>>? = null

    @ExperimentalPagingApi
    suspend fun searchImages(searchString: String): Flow<PagingData<Image>> {
        val lastResult = currentSearchResult
        if (searchString == currentSearch && lastResult != null) {
            return lastResult
        }
        currentSearch = searchString
        val newResult = searchUseCase.invoke(searchString)
            .cachedIn(viewModelScope)
        currentSearchResult = newResult
        return newResult
    }

}