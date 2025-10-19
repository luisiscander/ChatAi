package com.example.chatai.di

import android.content.Context
import com.example.chatai.data.local.ConversationRepositoryImpl
import com.example.chatai.data.local.UserPreferencesRepositoryImpl
import com.example.chatai.domain.repository.ConversationRepository
import com.example.chatai.domain.repository.UserPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindConversationRepository(
        conversationRepositoryImpl: ConversationRepositoryImpl
    ): ConversationRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AppModuleProvider {

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        @ApplicationContext context: Context
    ): UserPreferencesRepository {
        return UserPreferencesRepositoryImpl(context)
    }
}
