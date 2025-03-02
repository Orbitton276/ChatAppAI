package com.data.chatappai.domain.use_case

import com.data.chatappai.data.mapper.toDomainModel
import com.data.chatappai.domain.model.Message
import com.data.chatappai.domain.model.Result
import com.data.chatappai.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class GetAllMessagesUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(convId: Int): Flow<Result<List<Message>>> {
        return messageRepository.getMessages(convId)
            .map { users ->
                Result.Success(users.map { it.toDomainModel() }) as Result<List<Message>> }
            .onStart { emit(Result.Loading) }
            .catch { e -> emit(Result.Error(e.message ?: "Error occurred")) }
    }
}