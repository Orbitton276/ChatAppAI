package com.data.chatappai.data.repository

import com.data.chatappai.data.api.OpenAiApi
import com.data.chatappai.data.db.Conversation
import com.data.chatappai.data.db.ConversationDao
import com.data.chatappai.data.db.MessageEntity
import com.data.chatappai.data.db.User
import com.data.chatappai.data.dto.ChatRequestBody
import com.data.chatappai.data.dto.Choice
import com.data.chatappai.data.dto.Message
import com.data.chatappai.domain.model.Result
import com.data.chatappai.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ConversationRepoImpl @Inject constructor(
    private val conversationDao: ConversationDao,
    private val openAiApi: OpenAiApi
) : ConversationRepository {

    override suspend fun getAllConversations(): Flow<List<Conversation>> {
        return conversationDao.getAllConversations()
    }

    override suspend fun insertConversation(conversation: Conversation) {
        conversationDao.addConversation(conversation)
    }

    override suspend fun getConvById(convId: Int): Flow<Conversation> {
        return conversationDao.getConversationById(convId)
    }

    override suspend fun getFriendResponse(lastMessages: List<MessageEntity>, users: Map<String, User?>): Flow<Result<Choice>> {

        return flow {
            val result = try {
                val prepared = prepareGptMessages(lastMessages, users)
                val response = openAiApi.sendMessage(ChatRequestBody(prepared))
                response.choices[0]

            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Result.Error("error ${e.message}"))
                return@flow
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Result.Error("error ${e.message}"))
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Result.Error("error ${e.message}"))
                return@flow
            }
            emit(Result.Success(result))

        }


    }

    override suspend fun updateConversationById(convId: Int, lastMessageId: String, ts: Long) {
        conversationDao.updateLastMessage(convId = convId, lastMessageId = lastMessageId, lastUpdate = ts)
    }

    private fun prepareGptMessages(messages: List<MessageEntity>, users: Map<String, User?>): List<Message> {
        val systemMessage = Message(
            role = "system",
            content = """
            You are a supportive friend who remembers previous conversations. 
            You speak on behalf of the participants (except for "me").
            **Here are the participants in the conversation (with their IDs):**
            ${users.entries.joinToString(", ") { "[${it.key}]: ${it.value?.firstName ?: "Unknown"}" }}

            **When responding, ALWAYS respond as one of the participants, according to context. use the sender's ID** (e.g., "[123]: messageContent"). 
            Even if a name is given in the conversation, you should **respond with the sender's ID** in square brackets.
            If me refers to someone (e.g., "Hi John"), respond as that person.
            If me does not refer to anyone specifically or is unsure, you can respond generally, using the most relevant context available.
            NEVER refer to a participant directly as question, you can mention them but don't write something that expects a response from them
            **ALWAYS start your response with the sender's ID in square brackets, followed by a colon and your message.**
            Use natural, friendly language.
        """.trimIndent()
        )

        // Process the messages and format them for AI
        val formattedMessages = messages.takeLast(5).map { message ->
            // Determine the role based on whether the message is from the active user or someone else
            val senderRole = if (message.senderId == "me") "user" else "assistant"

            // Construct the message using the sender's ID for proper context
            val senderTag = "[${message.senderId}]"

            // Construct the message with the correct role and sender context
            Message(
                role = senderRole,  // Use "user" for the active user, "assistant" for others
                content = "$senderTag: ${message.content}"  // Include sender's ID and message content
            )
        }

        // Return system message along with formatted conversation messages
        return listOf(systemMessage) + formattedMessages
    }



}