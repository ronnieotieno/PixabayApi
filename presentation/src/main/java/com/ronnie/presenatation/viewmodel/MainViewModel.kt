package com.ronnie.presenatation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.FragmentNavigator
import androidx.paging.PagingData
import com.ronnie.commons.DEFAULT_SEARCH
import com.ronnie.domain.models.Image
import com.ronnie.domain.usecases.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase
) : ViewModel() {

    var currentDirectionExtras: FragmentNavigator.Extras? = null
    var selectedImage: Image? = null
    private val defaultSearch = DEFAULT_SEARCH
    var currentSearch = defaultSearch


    suspend fun searchImages(searchString: String): Flow<PagingData<Image>> {
        currentSearch = searchString
        return searchUseCase.invoke(searchString)
    }

}