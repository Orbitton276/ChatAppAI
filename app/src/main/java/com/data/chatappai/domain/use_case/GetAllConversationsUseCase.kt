package com.data.chatappai.domain.use_case

import com.data.chatappai.data.db.Conversation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import com.data.chatappai.domain.model.Result
import com.data.chatappai.domain.repository.ConversationRepository

class GetAllConversationsUseCase @Inject constructor(
    private val allConversationsRepository: ConversationRepository
) {
    suspend operator fun invoke(): Flow<Result<List<Conversation>>> {
        return allConversationsRepository.getAllConversations()
            .map { users -> Result.Success(users) as Result<List<Conversation>> }
            .onStart { emit(Result.Loading) }
            .catch { e -> emit(Result.Error(e.message ?: "Error occurred")) }
    }
}