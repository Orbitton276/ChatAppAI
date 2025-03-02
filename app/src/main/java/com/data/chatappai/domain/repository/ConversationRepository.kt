package com.data.chatappai.domain.repository

import com.data.chatappai.data.db.Conversation
import com.data.chatappai.data.db.MessageEntity
import com.data.chatappai.data.db.User
import com.data.chatappai.data.dto.Choice

import com.data.chatappai.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    suspend fun getAllConversations(): Flow<List<Conversation>>
    suspend fun insertConversation(conversation: Conversation)
    suspend fun getConvById(convId: Int) : Flow<Conversation>
    suspend fun getFriendResponse(lastMessages: List<MessageEntity>, users: Map<String, User?>): Flow<Result<Choice>>
    suspend fun updateConversationById(convId: Int, lastMessageId: String, ts: Long)
}