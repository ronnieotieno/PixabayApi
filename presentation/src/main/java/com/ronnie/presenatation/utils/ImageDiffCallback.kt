package com.ronnie.presenatation.utils

import androidx.recyclerview.widget.DiffUtil
import com.ronnie.domain.models.Image

 val imageDiffCallback = object : DiffUtil.ItemCallback<Image>() {
    override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem.id == newItem.id && oldItem.largeImageURL == oldItem.largeImageURL
    }

    override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
        return oldItem == newItem
    }
}