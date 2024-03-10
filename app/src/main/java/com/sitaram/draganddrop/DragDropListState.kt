package com.sitaram.draganddrop

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.Job

class DragDropListState(
    val lazyListState: LazyListState,
    private val onMove: (Int, Int) -> Unit // Callback function for move operation
) {
    // State variables
    private var draggedDistance by mutableFloatStateOf(0f)
    private var initiallyDraggedElement by mutableStateOf<LazyListItemInfo?>(null)
    var currentIndexOfDraggedItem by mutableStateOf<Int?>(null)

    // Get initial offsets of the dragged element
    private val initialOffsets: Pair<Int, Int>?
        get() = initiallyDraggedElement?.let {
            Pair(it.offset, it.offsetEnd)
        }

    // Calculate displacement of the dragged element
    val elementDisplacement: Float?
        get() = currentIndexOfDraggedItem
            ?.let {
                lazyListState.getVisibleItemInfoFor(absolute = it)
            }
            ?.let {
                    item -> (initiallyDraggedElement?.offset ?: 0f).toFloat() + draggedDistance - item.offset
            }

    // Get the current element being dragged
    private val currentElement: LazyListItemInfo?
        get() = currentIndexOfDraggedItem?.let {
            lazyListState.getVisibleItemInfoFor(absolute = it)
        }

    // Job for overscroll
    private var overScrollJob by mutableStateOf<Job?>(null)

    // Called when drag operation starts
    fun onDragStart(offset: Offset) {
        lazyListState.layoutInfo.visibleItemsInfo
            .firstOrNull { item ->
                offset.y.toInt() in item.offset..(item.offset + item.size)
            }?.also {
                currentIndexOfDraggedItem = it.index
                initiallyDraggedElement = it
            }
    }

    // Called when drag operation is interrupted
    fun onDragInterrupted() {
        draggedDistance = 0f
        currentIndexOfDraggedItem = null
        initiallyDraggedElement = null
        overScrollJob?.cancel()
    }

    // Called during drag operation
    fun onDrag(offset: Offset) {
        draggedDistance += offset.y

        initialOffsets?.let { (topOffset, bottomOffset) ->
            val startOffset = topOffset + draggedDistance
            val endOffset = bottomOffset + draggedDistance

            currentElement?.let { hovered ->
                lazyListState.layoutInfo.visibleItemsInfo
                    .filterNot { item ->
                        item.offsetEnd < startOffset || item.offset > endOffset || hovered.index == item.index
                    }
                    .firstOrNull { item ->
                        val delta = startOffset - hovered.offset
                        when {
                            delta > 0 -> (endOffset > item.offsetEnd)
                            else -> (startOffset < item.offset)
                        }
                    }?.also { item ->
                        currentIndexOfDraggedItem?.let { current ->
                            onMove.invoke(current, item.index)
                        }
                        currentIndexOfDraggedItem = item.index
                    }
            }
        }
    }

    // Check for overscroll
    fun checkForOverScroll(): Float {
        return initiallyDraggedElement?.let {
            val startOffset = it.offset + draggedDistance
            val endOffset = it.offsetEnd + draggedDistance

            return@let when {
                draggedDistance > 0 -> (endOffset - lazyListState.layoutInfo.viewportEndOffset).takeIf { diff ->
                    diff > 0
                }
                draggedDistance < 0 -> (startOffset - lazyListState.layoutInfo.viewportStartOffset).takeIf { diff ->
                    diff < 0
                }
                else -> null
            }
        } ?: 0f
    }
}

//class DragDropListState(
//    val lazyListState: LazyListState,
//    private val onMove: (Int, Int) -> Unit
//) {
//    private var draggedDistance by mutableFloatStateOf(0f)
//    private var initiallyDraggedElement by mutableStateOf<LazyListItemInfo?>(null)
//    var currentIndexOfDraggedItem by mutableStateOf<Int?>(null)
//    private val initialOffsets: Pair<Int, Int>?
//        get() = initiallyDraggedElement?.let {
//            Pair(it.offset, it.offsetEnd)
//        }
//    val elementDisplacement: Float?
//        get() = currentIndexOfDraggedItem
//            ?.let {
//                lazyListState.getVisibleItemInfoFor(absolute = it)
//            }
//            ?.let {
//                item -> (initiallyDraggedElement?.offset ?: 0f).toFloat() + draggedDistance - item.offset
//            }
//
//    private val currentElement: LazyListItemInfo?
//        get() = currentIndexOfDraggedItem?.let {
//            lazyListState.getVisibleItemInfoFor(absolute = it)
//        }
//
//    private var overScrollJob by mutableStateOf<Job?>(null)
//
//    fun onDragStart(offset: Offset) {
//        lazyListState.layoutInfo.visibleItemsInfo
//            .firstOrNull { item ->
//                offset.y.toInt() in item.offset..(item.offset + item.size)
//            }?.also {
//                currentIndexOfDraggedItem = it.index
//                initiallyDraggedElement = it
//            }
//    }
//
//    fun onDragInterrupted() {
//        draggedDistance = 0f
//        currentIndexOfDraggedItem = null
//        initiallyDraggedElement = null
//        overScrollJob?.cancel()
//    }
//
//    fun onDrag(offset: Offset) {
//        draggedDistance += offset.y
//
//        initialOffsets?.let { (topOffset, bottomOffset) ->
//            val startOffset = topOffset + draggedDistance
//            val endOffset = bottomOffset + draggedDistance
//
//            currentElement?.let { hovered ->
//                lazyListState.layoutInfo.visibleItemsInfo
//                    .filterNot { item ->
//                        item.offsetEnd < startOffset || item.offset > endOffset || hovered.index == item.index
//                    }
//                    .firstOrNull { item ->
//                        val delta = startOffset - hovered.offset
//                        when {
//                            delta > 0 -> (endOffset > item.offsetEnd)
//                            else -> (startOffset < item.offset)
//                        }
//                    }?.also { item ->
//                        currentIndexOfDraggedItem?.let { current ->
//                            onMove.invoke(current, item.index)
//                        }
//                        currentIndexOfDraggedItem = item.index
//                    }
//            }
//        }
//    }
//
//    fun checkForOverScroll(): Float {
//        return initiallyDraggedElement?.let {
//            val startOffset = it.offset + draggedDistance
//            val endOffset = it.offsetEnd + draggedDistance
//
//            return@let when {
//                draggedDistance > 0 -> (endOffset - lazyListState.layoutInfo.viewportEndOffset).takeIf { diff ->
//                    diff > 0
//                }
//                draggedDistance < 0 -> (startOffset - lazyListState.layoutInfo.viewportStartOffset).takeIf { diff ->
//                    diff < 0
//                }
//                else -> null
//            }
//        } ?: 0f
//    }
//}






















