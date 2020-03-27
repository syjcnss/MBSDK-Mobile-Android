package com.daimler.mbmobilesdk.example.utils

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

object Bindings {

    @Suppress("UNCHECKED_CAST")
    @JvmStatic
    @BindingAdapter("listAdapterItems")
    fun <T : MultipleRecyclerItem> bindItemsToListAdapter(
        list: RecyclerView,
        items: MutableList<T>?
    ) {
        items?.let {
            if (list.layoutManager == null) {
                list.layoutManager = LinearLayoutManager(list.context)
            }

            if (list.adapter is MultipleRecyclerItemListAdapter<*>) {
                (list.adapter as MultipleRecyclerItemListAdapter<T>).submitList(ArrayList(items))
            } else {
                val newAdapter = MultipleRecyclerItemListAdapter<T>()
                newAdapter.submitList(ArrayList(items))
                list.adapter = newAdapter
            }
        } ?: run {
            list.adapter = null
        }
    }

    @JvmStatic
    @BindingAdapter("imageBitmap")
    fun loadImage(imageView: ImageView, bitmap: Bitmap?) {
        bitmap?.let {
            imageView.setImageBitmap(bitmap)
        }
    }
}