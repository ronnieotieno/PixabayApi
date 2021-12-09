package com.ronnie.domain

import com.google.gson.annotations.SerializedName

data class ImageResponse(
    @SerializedName("hits")
    val images: List<Image>,
    val total: Int,
    val totalHits: Int
)