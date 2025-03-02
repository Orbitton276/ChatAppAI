package com.data.chatappai.domain.use_case

import com.data.chatappai.data.db.User
import com.data.chatappai.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUsersByIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userIds: List<Long>): Flow<List<User>> {
        return userRepository.getUsersByIds(userIds)
    }

}