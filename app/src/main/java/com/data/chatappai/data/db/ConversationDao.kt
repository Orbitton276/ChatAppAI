package com.data.chatappai.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao {

    @Upsert
    fun addConversation(conversation: Conversation)

    @Query("SELECT * FROM conversations ORDER BY lastUpdate DESC")
    fun getAllConversations(): Flow<List<Conversation>>

    @Query("SELECT * FROM conversations WHERE id = :id")
    fun getConversationById(id : Int): Flow<Conversation>

    @Query("UPDATE conversations SET lastMessageId = :lastMessageId, lastUpdate = :lastUpdate WHERE id = :convId")
    suspend fun updateLastMessage(convId: Int, lastMessageId: String, lastUpdate: Long)
}