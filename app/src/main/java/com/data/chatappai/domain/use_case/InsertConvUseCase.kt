package com.data.chatappai.domain.use_case

import com.data.chatappai.data.db.Conversation
import javax.inject.Inject
import com.data.chatappai.domain.repository.ConversationRepository

class InsertConvUseCase @Inject constructor(
    private val allConversationsRepository: ConversationRepository
) {
    suspend operator fun invoke(conv: Conversation) {
        allConversationsRepository.insertConversation(conv)
    }
}