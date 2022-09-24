package com.ronnie.data.mappers

import com.ronnie.data.models.ImagesEntity
import com.ronnie.data.models.dtos.ImageDto
import com.ronnie.domain.models.Image


fun ImagesEntity.toDomainImage(): Image {
    return Image(
        comments = comments,
        downloads = downloads,
        id = id,
        largeImageURL = largeImageURL,
        likes = likes,
        tags = tags,
        user = user,
        user_id = user_id,
        views = views,
        searchTerm = searchTerm
    )
}

fun ImageDto.toImageEntity(): ImagesEntity {
    return ImagesEntity(
        comments = comments,
        downloads = downloads,
        id = id,
        largeImageURL = largeImageURL,
        likes = likes,
        tags = tags,
        user = user,
        user_id = user_id,
        views = views,
        searchTerm = searchTerm
    )
}



