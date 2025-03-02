package com.data.chatappai.data.dto

data class OpenAiResponse(
    val choices: List<Choice>,
    val created: Int,
    val id: String,
    val model: String,
    val `object`: String
)