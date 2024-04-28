package com.sitaram.draganddrop.features.reorderable

data class ResourcesFolderState(
    val isLoading: Boolean? = false,
    var isData: List<FolderResponse>? = null,
    val error: String? = null
)

data class FolderResponse(
    val id: Int? = null,
    val name: String? = null,
)