package com.example.chatai.di

import android.content.Context
import com.example.chatai.data.local.dao.AiModelDao
import com.example.chatai.data.local.dao.ConversationDao
import com.example.chatai.data.local.dao.MessageDao
import com.example.chatai.data.local.database.ChatDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing Room Database and DAOs
 * Issues #127-135: Database infrastructure for caching and pagination
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideChatDatabase(
        @ApplicationContext context: Context
    ): ChatDatabase {
        return ChatDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideConversationDao(
        database: ChatDatabase
    ): ConversationDao {
        return database.conversationDao()
    }

    @Provides
    @Singleton
    fun provideMessageDao(
        database: ChatDatabase
    ): MessageDao {
        return database.messageDao()
    }

    @Provides
    @Singleton
    fun provideAiModelDao(
        database: ChatDatabase
    ): AiModelDao {
        return database.aiModelDao()
    }
}

