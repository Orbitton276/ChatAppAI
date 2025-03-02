package com.data.chatappai.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "conversations")
data class Conversation(
    val title: String = "",
    val participants: String, // Store as a comma-separated list or as JSON
    val lastMessageId: String? = null,
    val lastUpdate: Long = 0,
    var preview: String = "",
    @PrimaryKey(autoGenerate = true) val id: Int = 0
)