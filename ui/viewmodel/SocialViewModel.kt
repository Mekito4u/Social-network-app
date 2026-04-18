package com.app.modules.module4.pr4.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.modules.module4.pr4.domain.model.Comment
import com.app.modules.module4.pr4.domain.model.Post
import com.app.modules.module4.pr4.domain.model.PostUiState
import com.app.modules.module4.pr4.domain.model.UiState
import com.app.modules.module4.pr4.domain.utils.PostAvatarException
import com.app.modules.module4.pr4.domain.utils.PostCommentsException
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.emptyList
import kotlin.random.Random

class SocialViewModel : ViewModel() {
    private var posts: List<Post> = emptyList()
    private var comments: List<Comment> = emptyList()

    private val _uiState = MutableStateFlow<List<PostUiState>>(emptyList())
    val uiState = _uiState.asStateFlow()

    fun loadData(p: List<Post>, c: List<Comment>) {
        posts = p
        comments = c
        resetUiStates()
    }

    fun loadPostData(postId: Int) {
        viewModelScope.launch {
            val avatar = async {
                runCatching {
                    loadAvatar(postId)
                }
            }
            val comments = async {
                runCatching {
                    loadComments(postId)
                }
            }

            val avatarResult = avatar.await()
            val commentsResult = comments.await()

            avatarResult.exceptionOrNull()?.let { error ->
                println("===Error: Post $postId avatar error: ${error.message}")
            }
            commentsResult.exceptionOrNull()?.let { error ->
                println("===Error: Post $postId comments error: ${error.message}")
            }

            _uiState.value = _uiState.value.map {
                if (it.post.id == postId) {
                    it.copy(
                        isAvatarLoaded = if (avatarResult.isSuccess) UiState.READY else UiState.ERROR,
                        isCommentsLoaded = if (commentsResult.isSuccess) UiState.READY else UiState.ERROR,
                        comments = commentsResult.getOrNull() ?: emptyList()
                    )
                } else it
            }
        }
    }

    fun resetUiStates() {
        _uiState.value = posts.map { post ->
            PostUiState(
                post,
                isAvatarLoaded = UiState.LOADING,
                isCommentsLoaded = UiState.LOADING,
                comments = emptyList()
            )
        }
    }

    suspend fun loadAvatar(postId: Int): String {
        delay(Random.nextInt(0, 6000).toLong())
        if (Random.nextInt(1, 3) == 1) {
            throw PostAvatarException(postId = postId)
        }
        return posts.find { it.id == postId }?.avatarUrl ?: ""
    }

    suspend fun loadComments(postId: Int): List<Comment> {
        delay(Random.nextInt(0, 3000).toLong())
        if (Random.nextInt(1, 3) == 1) {
            throw PostCommentsException(postId = postId)
        }
        return comments.filter { it.postId == postId }
    }


    fun reloadPostData() {
        println("===Button: Reload Data")
        viewModelScope.coroutineContext.cancelChildren()
        resetUiStates()
        _uiState.value.forEach { state ->
            loadPostData(state.post.id)
        }
    }

    fun setAvatarError(postId: Int) {
        _uiState.value = _uiState.value.map {
            if (it.post.id == postId) {
                it.copy(
                    isAvatarLoaded = UiState.ERROR,
                )
            } else it
        }
    }
}