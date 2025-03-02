package com.data.chatappai.domain.use_case

import com.data.chatappai.data.db.MessageEntity
import com.data.chatappai.domain.repository.MessageRepository
import javax.inject.Inject

class GetMessagesByIdsUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(messages: List<String>): List<MessageEntity> {
        return messageRepository.getMessagesByIds(messages)
    }
}