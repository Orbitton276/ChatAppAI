package com.data.chatappai.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "avatar_url") val avatarUrl: String,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,  // Auto-incremented primary key
)