package com.ronnie.presenatation.mappers

import com.ronnie.domain.models.Image
import com.ronnie.presenatation.model.ImagePresentation


fun Image.toImagePresentation(): ImagePresentation {
    return ImagePresentation(
        comments, downloads, id, largeImageURL, likes, tags, user, user_id, views, searchTerm
    )
}

