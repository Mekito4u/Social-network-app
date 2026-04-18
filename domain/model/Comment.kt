package com.app.modules.module4.pr4.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: Int,
    val postId: Int,
    val name: String,
    val body: String
)
