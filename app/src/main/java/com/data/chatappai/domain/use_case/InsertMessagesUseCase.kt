package com.data.chatappai.domain.use_case

import com.data.chatappai.data.db.MessageEntity
import com.data.chatappai.domain.repository.ConversationRepository
import javax.inject.Inject
import com.data.chatappai.domain.repository.MessageRepository
import java.util.UUID

class InsertMessagesUseCase @Inject constructor(
    private val messageRepository: MessageRepository,
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(sender: String, content: String, convId: Int) {

        val msgId = UUID.randomUUID().toString()
        val ts = System.currentTimeMillis()
        messageRepository.insertMessage(
            MessageEntity(
                sender,
                content,
                ts,
                convId,
                msgId
            )
        )
        // go to the relevant conv and set the last message id
        conversationRepository.updateConversationById(convId, msgId, ts)
        println("insert msg $msgId with content $content")

    }
}