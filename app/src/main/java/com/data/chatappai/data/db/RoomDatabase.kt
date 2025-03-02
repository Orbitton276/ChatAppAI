package com.data.chatappai.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [User::class, Conversation::class, MessageEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
}