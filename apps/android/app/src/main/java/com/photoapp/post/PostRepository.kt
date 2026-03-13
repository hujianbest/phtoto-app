package com.photoapp.post

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
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
    private val postStore = mutableStateListOf<Post>()

    val posts: SnapshotStateList<Post> = postStore

    fun publish(
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

    fun clearForTest() {
        postStore.clear()
    }
}
