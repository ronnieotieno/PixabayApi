package com.ronnie.data.models.dtos

import com.google.gson.annotations.SerializedName

class ImageResponseDto(
    @SerializedName("hits")
    val images: List<ImageDto>,
    val total: Int,
    val totalHits: Int
)