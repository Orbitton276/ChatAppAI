package com.data.chatappai.domain.use_case

import com.data.chatappai.data.db.User
import com.data.chatappai.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject
import com.data.chatappai.domain.model.Result

class GetAllUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Flow<Result<List<User>>> {
        return userRepository.getAllUsers()
            .map { users -> Result.Success(users) as Result<List<User>> }
            .onStart { emit(Result.Loading) }
            .catch { e -> emit(Result.Error(e.message ?: "Error occurred")) }
    }

}