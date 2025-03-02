package com.data.chatappai.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    val senderId: String,
    val content: String,
    val timestamp: Long,
    val convId: Int,
    @PrimaryKey var id: String,
    val status: String = "", // Store the status as a string for simplicity
)