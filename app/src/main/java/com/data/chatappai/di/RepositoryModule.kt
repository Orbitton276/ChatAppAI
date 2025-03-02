package com.data.chatappai.di

import com.data.chatappai.data.repository.ConversationRepoImpl
import com.data.chatappai.data.repository.MessageRepositoryImpl
import com.data.chatappai.data.repository.UserRepositoryImpl
import com.data.chatappai.domain.repository.ConversationRepository
import com.data.chatappai.domain.repository.MessageRepository
import com.data.chatappai.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAllConvRepository(
        allConversationsRepoImpl: ConversationRepoImpl
    ) : ConversationRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(
        messageRepositoryImpl: MessageRepositoryImpl
    ) : MessageRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ) : UserRepository
}