package com.example.androidinstrumentation
import com.jio.otel.JioOkHttpAutoInstrumentation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
// .let { JioOkHttpAutoInstrumentation.addTracing(it) }
object BackendApi {
    private val client = OkHttpClient.Builder()
        .let { JioOkHttpAutoInstrumentation.addTracing(it) }
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val baseUrl: String
        get() = BuildConfig.DEMO_API_BASE_URL.trimEnd('/')

    data class ApiCard(
        val meta: String,
        val title: String,
        val body: String,
    )

    private suspend fun executeGet(url: String): JSONObject = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            val bodyString = response.body.string()
            if (!response.isSuccessful) {
                throw IOException("HTTP ${response.code}: $bodyString")
            }
            JSONObject(bodyString)
        }
    }

    private suspend fun executePost(url: String, jsonBody: JSONObject): JSONObject =
        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .post(
                    jsonBody.toString()
                        .toRequestBody("application/json; charset=utf-8".toMediaType()),
                )
                .build()

            client.newCall(request).execute().use { response ->
                val bodyString = response.body.string()
                if (!response.isSuccessful) {
                    throw IOException("HTTP ${response.code}: $bodyString")
                }
                JSONObject(bodyString)
            }
        }

    suspend fun getUniquePost(postId: Int): ApiCard {
        val json = executeGet("$baseUrl/api/v1/unique-post/$postId")
        return ApiCard(
            meta = "GET /api/v1/unique-post/$postId",
            title = json.getString("title"),
            body = json.getString("body"),
        )
    }

    suspend fun getServerInfo(): ApiCard {
        val json = executeGet("$baseUrl/api/v1/server-info")
        return ApiCard(
            meta = json.getString("meta"),
            title = json.getString("title"),
            body = json.getString("body"),
        )
    }

    suspend fun postEchoNote(note: String): ApiCard {
        val payload = JSONObject().put("note", note)
        val json = executePost("$baseUrl/api/v1/echo-note", payload)
        return ApiCard(
            meta = json.getString("meta"),
            title = json.getString("title"),
            body = json.getString("body"),
        )
    }

    suspend fun postCreateTicket(title: String, priority: String): ApiCard {
        val payload = JSONObject()
            .put("title", title)
            .put("priority", priority)
        val json = executePost("$baseUrl/api/v1/create-ticket", payload)
        return ApiCard(
            meta = json.getString("meta"),
            title = json.getString("title"),
            body = json.getString("body"),
        )
    }

}
