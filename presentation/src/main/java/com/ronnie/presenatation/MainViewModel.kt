package com.ronnie.presenatation

import androidx.lifecycle.ViewModel
import com.ronnie.domain.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor():ViewModel() {
    var selectedImage: Image? = null
}