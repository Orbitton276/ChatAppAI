package com.data.chatappai.domain.model


import com.data.chatappai.data.db.User

data class Message(
    val senderId: String,
    val content: String,
    val timestamp: Long,
    val convId: Int,
    val id: String,
    val status: String = "", // Store the status as a string for simplicity
    val user: User? = null,
)