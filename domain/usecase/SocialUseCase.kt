package com.app.modules.module4.pr4.domain.usecase

import android.content.Context
import com.app.modules.module4.pr4.domain.model.Comment
import com.app.modules.module4.pr4.domain.model.Post
import kotlinx.serialization.json.Json

fun loadPostsFromJson(context: Context): List<Post> {
    return try {
        val json = context.assets.open("social_posts.json").bufferedReader().use { it.readText() }
        Json.decodeFromString<List<Post>>(json)
    } catch (e: Exception) {
        println(e.message)
        return emptyList()
    }
}

fun loadCommentsFromJson(context: Context): List<Comment> {
    return try {
        val json = context.assets.open("comments.json").bufferedReader().use { it.readText() }
        Json.decodeFromString<List<Comment>>(json)
    } catch (e: Exception) {
        println(e.message)
        return emptyList()
    }
}