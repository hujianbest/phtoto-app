package com.photoapp.network

import com.photoapp.BuildConfig
import com.photoapp.post.Post
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

private data class HttpResult(
    val code: Int,
    val body: String
)

object ApiClient {
    private fun endpoint(path: String): String =
        BuildConfig.API_BASE_URL.trimEnd('/') + path

    suspend fun registerOrLogin(email: String, password: String): String? = withContext(Dispatchers.IO) {
        val normalizedEmail = email.trim().lowercase()
        val payload = JSONObject()
            .put("email", normalizedEmail)
            .put("password", password)

        val loginResult = request(
            method = "POST",
            url = endpoint("/auth/login"),
            body = payload.toString()
        )
        val loginToken = readToken(loginResult)
        if (loginToken != null) {
            return@withContext loginToken
        }

        // If user doesn't exist yet, register and use returned token.
        val registerResult = request(
            method = "POST",
            url = endpoint("/auth/register"),
            body = payload.toString()
        )
        readToken(registerResult)
    }

    suspend fun createPost(
        title: String,
        intent: String,
        imageUrl: String,
        exifSummary: String
    ): Boolean = withContext(Dispatchers.IO) {
        val payload = JSONObject()
            .put("title", title.trim())
            .put("description", intent.trim())
            .put("imageUrl", imageUrl.trim())
            .put("intent", intent.trim())
            .put("exif", JSONObject().put("shutter", exifSummary.trim()))

        val result = request(
            method = "POST",
            url = endpoint("/posts"),
            body = payload.toString()
        )
        result.code == 201
    }

    suspend fun createPostReport(
        postId: String,
        reason: String
    ): Boolean = withContext(Dispatchers.IO) {
        val payload = JSONObject()
            .put("targetType", "post")
            .put("targetId", postId.trim())
            .put("reason", reason.trim())

        val result = request(
            method = "POST",
            url = endpoint("/reports"),
            body = payload.toString()
        )
        result.code == 201
    }

    suspend fun joinWeeklyChallenge(email: String): String? = withContext(Dispatchers.IO) {
        val payload = JSONObject().put("email", email.trim().lowercase())
        val result = request(
            method = "POST",
            url = endpoint("/challenges/weekly/join"),
            body = payload.toString()
        )
        if (result.code !in 200..299 || result.body.isBlank()) {
            return@withContext null
        }
        runCatching {
            JSONObject(result.body).optString("joinedAt", "")
        }.getOrDefault("").takeIf { it.isNotBlank() }
    }

    suspend fun fetchRecommendedPosts(): List<Post> = withContext(Dispatchers.IO) {
        val result = request(
            method = "GET",
            url = endpoint("/feed/recommended")
        )
        if (result.code !in 200..299 || result.body.isBlank()) {
            return@withContext emptyList()
        }

        val root = JSONObject(result.body)
        val items = root.optJSONArray("items") ?: JSONArray()
        buildList {
            for (index in 0 until items.length()) {
                val item = items.optJSONObject(index) ?: continue
                add(
                    Post(
                        id = item.optString("id", ""),
                        title = item.optString("title", ""),
                        intent = item.optString("intent", ""),
                        imageUrl = item.optString("imageUrl", ""),
                        exifSummary = buildExifSummary(item.optJSONObject("exif")),
                        authorName = "社区用户"
                    )
                )
            }
        }.filter { post ->
            post.id.isNotBlank() &&
                post.title.isNotBlank() &&
                post.intent.isNotBlank() &&
                post.imageUrl.isNotBlank()
        }
    }

    private fun buildExifSummary(exif: JSONObject?): String {
        if (exif == null) {
            return "未知参数"
        }
        val segments = mutableListOf<String>()
        exif.optDouble("aperture").takeIf { it > 0 }?.let { segments += "f/$it" }
        exif.optInt("iso").takeIf { it > 0 }?.let { segments += "ISO$it" }
        exif.optString("shutter").takeIf { it.isNotBlank() }?.let { segments += it }
        return if (segments.isEmpty()) "未知参数" else segments.joinToString(" ")
    }

    private fun readToken(result: HttpResult): String? {
        if (result.code !in 200..299 || result.body.isBlank()) {
            return null
        }
        return runCatching {
            JSONObject(result.body).optString("token", "")
        }.getOrDefault("").takeIf { it.isNotBlank() }
    }

    private fun request(
        method: String,
        url: String,
        body: String? = null
    ): HttpResult {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 2_000
            readTimeout = 2_000
            setRequestProperty("accept", "application/json")
            if (body != null) {
                doOutput = true
                setRequestProperty("content-type", "application/json")
            }
        }

        return try {
            if (body != null) {
                OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                    writer.write(body)
                }
            }

            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val responseBody = stream?.bufferedReader(Charsets.UTF_8)?.use(BufferedReader::readText).orEmpty()
            HttpResult(code = code, body = responseBody)
        } finally {
            connection.disconnect()
        }
    }
}
