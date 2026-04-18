package com.app.modules.module4.pr4.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.app.modules.module4.pr4.domain.model.UiState
import com.app.modules.module4.pr4.domain.usecase.loadCommentsFromJson
import com.app.modules.module4.pr4.domain.usecase.loadPostsFromJson
import com.app.modules.module4.pr4.ui.viewmodel.SocialViewModel

@Preview
@Composable
fun SocialView(
    viewModel: SocialViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val loadedPosts = loadPostsFromJson(context)
        val loadedComments = loadCommentsFromJson(context)

        viewModel.loadData(loadedPosts, loadedComments)
        println("===Data: $loadedPosts")
        println("===Data: $loadedComments")
    }

    val posts by viewModel.uiState.collectAsState()

    Scaffold { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Social Network", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier=Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.75f)
            ) {
                items(posts) { post ->
                    LaunchedEffect(post.post.id) {
                        if (post.isAvatarLoaded == UiState.LOADING || post.isCommentsLoaded == UiState.LOADING) {
                            viewModel.loadPostData(post.post.id)
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.LightGray,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    Modifier
                                        .height(64.dp)
                                        .width(64.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    when (post.isAvatarLoaded) {
                                        UiState.READY ->
                                            AsyncImage(
                                                model = post.post.avatarUrl,
                                                contentDescription = "Avatar",
                                                error = painterResource(android.R.drawable.ic_menu_report_image),
                                                onError = {
                                                    viewModel.setAvatarError(post.post.id)
                                                }
                                            )

                                        UiState.LOADING ->
                                            Text(
                                                "Loading",
                                                color = Color.DarkGray,
                                                style = MaterialTheme.typography.bodyMedium
                                            )

                                        UiState.ERROR ->
                                            Text(
                                                "Error",
                                                color = Color.Red,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                    }
                                }

                                Spacer(Modifier.width(8.dp))

                                Text(
                                    text = "User: ${post.post.userId}",
                                    style = MaterialTheme.typography.titleLarge
                                )

                                Spacer(Modifier.width(128.dp))
                            }

                            Text(
                                text = "Тема: ${post.post.title}",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = post.post.body,
                                style = MaterialTheme.typography.titleMedium
                            )

                            HorizontalDivider(
                                color = Color.Gray,
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Text(
                                text = "Комментарии:",
                                style = MaterialTheme.typography.titleSmall
                            )

                            Column(
                                Modifier.fillMaxWidth()
                            ) {
                                when (post.isCommentsLoaded) {
                                    UiState.READY ->
                                        post.comments.forEach { comm ->
                                            Row{
                                                Text(
                                                    text = "${comm.name}: ",
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                                Text(
                                                    text = comm.body,
                                                    style = MaterialTheme.typography.bodyLarge
                                                )
                                            }
                                        }

                                    UiState.LOADING ->
                                        Text(
                                            "Loading",
                                            color = Color.DarkGray,
                                            style = MaterialTheme.typography.bodyMedium
                                        )

                                    UiState.ERROR ->
                                        Text(
                                            "Error",
                                            color = Color.Red,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier=Modifier.height(8.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(
                    onClick = { viewModel.reloadPostData() }
                ) {
                    Text(
                        text = "Обновить", style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }
    }
}