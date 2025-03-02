package com.data.chatappai.domain.use_case

import com.data.chatappai.data.db.User
import com.data.chatappai.domain.repository.UserRepository
import javax.inject.Inject

class InsertUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(users: List<User>) {
        userRepository.insertUser(users)
    }
}