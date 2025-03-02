package com.data.chatappai.di

import android.content.Context
import androidx.room.Room
import com.data.chatappai.data.db.AppDatabase
import com.data.chatappai.data.db.ConversationDao
import com.data.chatappai.data.db.MessageDao
import com.data.chatappai.data.db.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    fun provideConvDao(appDatabase: AppDatabase): ConversationDao {
        return appDatabase.conversationDao()
    }
    @Provides
    fun provideMessageDao(appDatabase: AppDatabase): MessageDao {
        return appDatabase.messageDao()
    }
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration() // Use this to migrate schema if needed
            .build()
    }


}