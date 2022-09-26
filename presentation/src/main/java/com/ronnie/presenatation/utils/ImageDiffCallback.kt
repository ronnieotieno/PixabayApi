package com.ronnie.presenatation.utils

import androidx.recyclerview.widget.DiffUtil
import com.ronnie.domain.models.Image
import com.ronnie.presenatation.model.ImagePresentation

val imageDiffCallback = object : DiffUtil.ItemCallback<ImagePresentation>() {
    override fun areItemsTheSame(oldItem: ImagePresentation, newItem: ImagePresentation): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ImagePresentation, newItem: ImagePresentation): Boolean {
        return oldItem == newItem
    }
}