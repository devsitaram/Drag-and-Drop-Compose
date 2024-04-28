package com.sitaram.draganddrop.features

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sitaram.draganddrop.features.reorderable.ItemPosition
import com.sitaram.draganddrop.features.reorderable.ResourcesFolderState

class ImageListViewModel : ViewModel() {

    // Function to reorder images
    var images by mutableStateOf(List(20) { "https://picsum.photos/seed/compose$it/200/300" })
    fun onMove(from: ItemPosition, to: ItemPosition) {
        images = images.toMutableList().apply {
            add(
                images.indexOfFirst { it == to.key },
                removeAt(images.indexOfFirst { it == from.key })
            )
        }
    }

    fun canDragOver(draggedOver: ItemPosition, dragging: ItemPosition) = images.any {
        it == draggedOver.key
    }
}