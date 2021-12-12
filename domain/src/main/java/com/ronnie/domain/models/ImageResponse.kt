package com.ronnie.domain.models

import com.google.gson.annotations.SerializedName

data class ImageResponse(
    @SerializedName("hits")
    val images: List<Image>,
    val total: Int,
    val totalHits: Int
)