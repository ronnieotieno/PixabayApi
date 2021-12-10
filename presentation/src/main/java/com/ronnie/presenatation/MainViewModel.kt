package com.ronnie.presenatation

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.ronnie.domain.Image
import com.ronnie.domain.usecases.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val searchUseCase: SearchUseCase):ViewModel() {

    var selectedImage: Image? = null
    var currentSearch = "fruits"

    suspend fun searchImages(searchString: String): Flow<PagingData<Image>> {
        return searchUseCase.invoke(searchString)
    }
}