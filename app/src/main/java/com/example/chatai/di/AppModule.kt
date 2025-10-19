package com.example.chatai.di

import android.content.Context
import androidx.room.Room
import com.example.chatai.data.local.ChatDatabase
import com.example.chatai.data.local.ConversationRepositoryImpl
import com.example.chatai.data.local.UserPreferencesRepositoryImpl
import com.example.chatai.data.local.dao.ConversationDao
import com.example.chatai.data.local.dao.MessageDao
import com.example.chatai.domain.repository.ConversationRepository
import com.example.chatai.domain.repository.UserPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        @ApplicationContext context: Context
    ): UserPreferencesRepository {
        return UserPreferencesRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideChatDatabase(@ApplicationContext context: Context): ChatDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            ChatDatabase::class.java,
            "chat_database"
        ).build()
    }

    @Provides
    fun provideConversationDao(database: ChatDatabase): ConversationDao {
        return database.conversationDao()
    }

    @Provides
    fun provideMessageDao(database: ChatDatabase): MessageDao {
        return database.messageDao()
    }

    @Provides
    @Singleton
    fun provideConversationRepository(
        conversationDao: ConversationDao,
        messageDao: MessageDao
    ): ConversationRepository {
        return ConversationRepositoryImpl(conversationDao, messageDao)
    }
}
