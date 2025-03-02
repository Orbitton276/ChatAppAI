package com.data.chatappai.data.repository

import com.data.chatappai.data.db.MessageDao
import com.data.chatappai.data.db.MessageEntity
import com.data.chatappai.domain.repository.MessageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val messageDao: MessageDao
) : MessageRepository {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override suspend fun getMessages(convId: Int): Flow<List<MessageEntity>> {
        return messageDao.getAllMessagesForConvId(convId)
    }

    override suspend fun insertMessage(messageEntity: MessageEntity) {
        repositoryScope.launch {
            messageDao.insertMessage(messageEntity)
        }
    }

    override suspend fun getMessageHistory(convId: Int): List<MessageEntity> {
        return messageDao.getMessageHistory(convId)
    }

    override suspend fun getMessagesByIds(messages: List<String>): List<MessageEntity> {
        return messageDao.getMessagesByIds(messages)
    }


}