package com.app.modules.module4.pr4.domain.model

data class PostUiState(
    val post: Post,
    val isAvatarLoaded: UiState = UiState.LOADING,
    val isCommentsLoaded: UiState = UiState.LOADING,
    val comments: List<Comment>
)
