/*
 * Copyright 2022 André Claßen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitaram.draganddrop.features

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.sitaram.draganddrop.features.reorderable.ReorderableItem
import com.sitaram.draganddrop.features.reorderable.detectReorder
import com.sitaram.draganddrop.features.reorderable.rememberReorderableLazyListState
import com.sitaram.draganddrop.features.reorderable.reorderable

@Composable
fun ReorderImageList(
    modifier: Modifier = Modifier,
    vm: ImageListViewModel = viewModel(),
) {

    val image = vm.images
    val context = LocalContext.current
    val state = rememberReorderableLazyListState(
        onMove = { from, to ->
            vm.onMove(from, to)
        },
        canDragOver = { draggedOver, dragging ->
            vm.canDragOver(draggedOver, dragging)
        },
        onDragEnd = { startIndex, endIndex ->
            Toast.makeText(context, "Success: startIndex: $startIndex and endIndex: $endIndex", Toast.LENGTH_SHORT).show()
        }
    )


        LazyColumn(
            state = state.listState,
            modifier = modifier
                .then(Modifier.reorderable(state))
        ) {
            items(image, { it }) { item ->
                ReorderableItem(state, item) { isDragging ->
                    val elevation = animateDpAsState(if (isDragging) 8.dp else 0.dp, label = "")
                    Column(
                        modifier = Modifier
                            .shadow(elevation.value)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)

                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                Icons.AutoMirrored.Filled.List,
                                "",
                                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground),
                                modifier = Modifier.detectReorder(state)
                            )
                            Image(
                                painter = rememberAsyncImagePainter(item),
                                contentDescription = null,
                                modifier = Modifier.size(128.dp)
                            )
                            Text(
                                text = item,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        Divider()
                    }
                }
            }
        }
}