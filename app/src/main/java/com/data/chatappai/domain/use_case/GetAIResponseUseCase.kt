package com.data.chatappai.domain.use_case

import com.data.chatappai.data.db.User
import com.data.chatappai.domain.model.Result
import com.data.chatappai.domain.repository.ConversationRepository
import com.data.chatappai.domain.repository.MessageRepository
import com.data.chatappai.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


class GetAIResponseUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
    private val insertMessagesUseCase: InsertMessagesUseCase,
    private val userRepository: UserRepository,
    @Singleton private val applicationScope: CoroutineScope

) {
    operator fun invoke(convId: Int) {
        applicationScope.launch {
            println("GetAIResponseUseCase started")

            // Fetch message history
            val history = messageRepository.getMessageHistory(convId)

            val convDeferred = async {
                conversationRepository.getConvById(convId).first()
            }
            val conversation = convDeferred.await()
            val participants = conversation.participants.split(",")
            println("GetAIResponseUseCase fetched history")

            // Use coroutineScope to fetch users in parallel
            val users = mutableMapOf<String, User?>()

            val deferredUsers = participants
                .map { senderId ->
                    async {
                        val user = userRepository.getUserById(senderId.toInt()).firstOrNull()
                        senderId to user
                    }
                }

            // Use coroutineScope to wait for all async tasks to complete
            coroutineScope {
                deferredUsers.map { it.await() } // This will block until all async tasks are complete
                    .forEach { (senderId, user) ->
                        users[senderId] = user
                    }
            }

            // Now that users are fetched, get the AI response
            conversationRepository.getFriendResponse(history.reversed(), users).collect { result ->
                when (result) {
                    is Result.Error -> { /* Handle error */ }
                    Result.Loading -> { /* Handle loading */ }
                    is Result.Success -> {
                        println("GetAIResponseUseCase result")
                        delay(1000)

                        val responseText = result.data.message.content.trim()

                        // Extract senderId and message using regex
                        val senderPattern = """^\[(\d+|me)]:\s*(.*)$""".toRegex()
                        val match = senderPattern.find(responseText)

                        if (match != null) {
                            val senderId = match.groupValues[1]  // Extracted senderId (can be "me" or number)
                            val messageContent = match.groupValues[2].trim()  // Extracted content

                            // Insert the message with only the content, ignoring "me" as sender
                            if (senderId != "me") {
                                insertMessagesUseCase(senderId, messageContent, convId)
                            } else {
                                insertMessagesUseCase(users.keys.firstOrNull { it != "me" } ?: "default_sender", messageContent, convId)
                            }
                        } else {
                            // If no valid sender is found, just remove "[me]: " manually if present
                            val cleanedMessage = responseText.removePrefix("[me]:").trim()
                            insertMessagesUseCase(users.keys.firstOrNull { it != "me" } ?: "default_sender", cleanedMessage, convId)
                        }
                    }
                }
            }



        }
    }


}