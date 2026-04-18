package com.app.modules.module4.pr4.domain.utils

class PostCommentsException(
    private val postId: Int
) : Exception("Failed to load comments for post $postId")