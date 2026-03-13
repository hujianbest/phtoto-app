package com.photoapp.post

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.photoapp.network.ApiClient
import java.util.UUID

data class Post(
    val id: String,
    val title: String,
    val intent: String,
    val imageUrl: String,
    val exifSummary: String,
    val authorName: String
)

object PostRepository {
    enum class FeedMode {
        RECOMMENDED,
        FOLLOWING
    }

    private val postStore = mutableStateListOf<Post>()

    val posts: SnapshotStateList<Post> = postStore

    fun publishLocal(
        title: String,
        intent: String,
        imageUrl: String,
        exifSummary: String,
        authorName: String
    ) {
        val safeTitle = title.trim()
        val safeIntent = intent.trim()
        val safeImageUrl = imageUrl.trim()
        val safeExifSummary = exifSummary.trim()
        val safeAuthorName = authorName.trim()
        if (
            safeTitle.isEmpty() ||
            safeIntent.isEmpty() ||
            safeImageUrl.isEmpty() ||
            safeExifSummary.isEmpty() ||
            safeAuthorName.isEmpty()
        ) {
            return
        }

        postStore.add(
            index = 0,
            element = Post(
                id = UUID.randomUUID().toString(),
                title = safeTitle,
                intent = safeIntent,
                imageUrl = safeImageUrl,
                exifSummary = safeExifSummary,
                authorName = safeAuthorName
            )
        )
    }

    suspend fun publish(
        title: String,
        intent: String,
        imageUrl: String,
        exifSummary: String,
        authorName: String,
        viewerEmail: String = ""
    ) {
        // Keep local UX responsive first.
        publishLocal(title, intent, imageUrl, exifSummary, authorName)
        runCatching {
            ApiClient.createPost(
                title = title,
                intent = intent,
                imageUrl = imageUrl,
                exifSummary = exifSummary,
                authorEmail = viewerEmail
            )
        }
    }

    suspend fun syncFromRemote(
        mode: FeedMode = FeedMode.RECOMMENDED,
        viewerEmail: String = ""
    ) {
        val remotePosts = runCatching {
            when (mode) {
                FeedMode.RECOMMENDED -> ApiClient.fetchRecommendedPosts()
                FeedMode.FOLLOWING -> ApiClient.fetchFollowingPosts(viewerEmail)
            }
        }.getOrDefault(emptyList())
        if (remotePosts.isEmpty()) {
            return
        }
        postStore.clear()
        postStore.addAll(remotePosts)
    }

    fun clearForTest() {
        postStore.clear()
    }
}
