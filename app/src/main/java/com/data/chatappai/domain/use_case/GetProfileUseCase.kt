package com.data.chatappai.domain.use_case

import com.data.chatappai.data.db.Conversation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import com.data.chatappai.domain.model.Result
import com.data.chatappai.domain.repository.ConversationRepository

class GetProfileUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(convId: Int): Flow<Result<Conversation>> {
        return conversationRepository.getConvById(convId)
            .map {
                users -> Result.Success(users) as Result<Conversation>
            }
            .onStart { emit(Result.Loading) }
            .catch { e -> emit(Result.Error(e.message ?: "Error occurred")) }
    }
}