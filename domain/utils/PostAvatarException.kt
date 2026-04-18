package com.app.modules.module4.pr4.domain.utils

class PostAvatarException(
    private val postId: Int
) : Exception("Failed to load avatar for post $postId")