package com.ronnie.data.models.dtos

import com.google.gson.annotations.SerializedName
import com.ronnie.domain.models.Image

class ImageResponseDto(
    @SerializedName("hits")
    val images: List<ImageDto>,
    val total: Int,
    val totalHits: Int
)