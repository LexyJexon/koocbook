package com.example.koocbook

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class APIHelper(private val baseUrl: String) {

    private val client = OkHttpClient()

    // Метод для выполнения GET запроса
    @Throws(IOException::class)
    suspend fun getRequest(endpoint: String): Response {
        val request = Request.Builder()
            .url("$baseUrl/$endpoint")
            .build()

        return executeRequest(request)
    }

    // Метод для выполнения POST запроса
    @Throws(IOException::class)
    suspend fun postRequest(endpoint: String, jsonBody: String): Response {
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = jsonBody.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$baseUrl/$endpoint")
            .post(requestBody)
            .build()

        return executeRequest(request)
    }

    // Метод для выполнения PUT запроса
    @Throws(IOException::class)
    suspend fun putRequest(endpoint: String, jsonBody: String): Response {
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = jsonBody.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$baseUrl/$endpoint")
            .put(requestBody)
            .build()

        return executeRequest(request)
    }

    // Метод для выполнения DELETE запроса
    @Throws(IOException::class)
    suspend fun deleteRequest(endpoint: String): Response {
        val request = Request.Builder()
            .url("$baseUrl/$endpoint")
            .delete()
            .build()

        return executeRequest(request)
    }

    @Throws(IOException::class)
    suspend fun executeRequest(request: Request): Response {
        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response
            } else {
                throw IOException("Unexpected code $response")
            }
        }
    }
}
