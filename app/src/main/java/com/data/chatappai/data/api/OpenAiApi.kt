package com.data.chatappai.data.api

import com.data.chatappai.data.dto.ChatRequestBody
import com.data.chatappai.data.dto.OpenAiResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAiApi {

    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun sendMessage(@Body requestBody: ChatRequestBody): OpenAiResponse


    companion object {
        const val BASE_URL = "https://api.openai.com/"
    }
}