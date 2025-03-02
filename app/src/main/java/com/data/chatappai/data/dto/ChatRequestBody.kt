package com.data.chatappai.data.dto

data class ChatRequestBody(
    val messages: List<Message>,
    val model: String = "gpt-3.5-turbo",
    val temperature: Double = 0.5
)