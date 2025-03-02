package com.data.chatappai.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow


@Dao
interface MessageDao {
    @Upsert
    fun insertMessage(message: MessageEntity)

    @Query("SELECT * from messages where convId = :convId")
    fun getAllMessagesForConvId(convId: Int): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE convId = :convId ORDER BY timestamp DESC LIMIT 5")
    fun getMessageHistory(convId: Int): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE id IN (:messageIds)")
    suspend fun getMessagesByIds(messageIds: List<String>): List<MessageEntity>
}