package com.data.chatappai.domain.repository

import com.data.chatappai.data.db.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getAllUsers() : Flow<List<User>>
    suspend fun getUserById(userId: Int) : Flow<User>
    suspend fun insertUser(users: List<User>)
    suspend fun getUsersByIds(userIds: List<Long>) : Flow<List<User>>
}

