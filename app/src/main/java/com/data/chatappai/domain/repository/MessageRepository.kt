package com.data.chatappai.domain.repository

import com.data.chatappai.data.db.MessageEntity
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun getMessages(convId: Int) : Flow<List<MessageEntity>>
    suspend fun insertMessage(messageEntity: MessageEntity)
    suspend fun getMessageHistory(convId: Int) : List<MessageEntity>
    suspend fun getMessagesByIds(messages : List<String>) : List<MessageEntity>
}