package com.example.chatai.di

import android.content.Context
import com.example.chatai.data.local.AiModelRepositoryImpl
import com.example.chatai.data.local.ConversationRepositoryImpl
import com.example.chatai.data.local.UserPreferencesRepositoryImpl
import com.example.chatai.domain.repository.AiModelRepository
import com.example.chatai.domain.repository.ConversationRepository
import com.example.chatai.domain.repository.UserPreferencesRepository
import com.example.chatai.domain.usecase.*
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

    @Binds
    @Singleton
    abstract fun bindAiModelRepository(
        aiModelRepositoryImpl: AiModelRepositoryImpl
    ): AiModelRepository
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
    
    @Provides
    @Singleton
    fun provideSearchConversationsUseCase(
        conversationRepository: ConversationRepository
    ): SearchConversationsUseCase {
        return SearchConversationsUseCase(conversationRepository)
    }
    
    @Provides
    @Singleton
    fun provideGetConversationByIdUseCase(
        conversationRepository: ConversationRepository
    ): GetConversationByIdUseCase {
        return GetConversationByIdUseCase(conversationRepository)
    }
    
    @Provides
    @Singleton
    fun provideGetMessagesSyncUseCase(
        conversationRepository: ConversationRepository
    ): GetMessagesSyncUseCase {
        return GetMessagesSyncUseCase(conversationRepository)
    }
    
    @Provides
    @Singleton
    fun provideGetAvailableModelsUseCase(
        aiModelRepository: AiModelRepository
    ): GetAvailableModelsUseCase {
        return GetAvailableModelsUseCase(aiModelRepository)
    }
    
    @Provides
    @Singleton
    fun provideSendMessageUseCase(
        conversationRepository: ConversationRepository
    ): SendMessageUseCase {
        return SendMessageUseCase(conversationRepository)
    }
    
    @Provides
    @Singleton
    fun provideGetAiResponseUseCase(
        conversationRepository: ConversationRepository
    ): GetAiResponseUseCase {
        return GetAiResponseUseCase(conversationRepository)
    }
    
    @Provides
    @Singleton
    fun provideCheckNetworkConnectionUseCase(
        @ApplicationContext context: Context
    ): CheckNetworkConnectionUseCase {
        return CheckNetworkConnectionUseCase(context)
    }
    
    @Provides
    @Singleton
    fun provideValidateApiKeyConnectionUseCase(
        openRouterApiService: com.example.chatai.data.remote.api.OpenRouterApiService
    ): ValidateApiKeyConnectionUseCase {
        return ValidateApiKeyConnectionUseCase(openRouterApiService)
    }
    
    @Provides
    @Singleton
    fun provideStreamAiResponseUseCase(): StreamAiResponseUseCase {
        return StreamAiResponseUseCase()
    }
    
    @Provides
    @Singleton
    fun provideCancelStreamingUseCase(): CancelStreamingUseCase {
        return CancelStreamingUseCase()
    }
    
    @Provides
    @Singleton
    fun provideGetMessagesUseCase(
        conversationRepository: ConversationRepository
    ): GetMessagesUseCase {
        return GetMessagesUseCase(conversationRepository)
    }
    
    @Provides
    @Singleton
    fun provideGetMessagesWithPaginationUseCase(
        conversationRepository: ConversationRepository
    ): GetMessagesWithPaginationUseCase {
        return GetMessagesWithPaginationUseCase(conversationRepository)
    }
    
    @Provides
    @Singleton
    fun provideGetThemeModeUseCase(
        userPreferencesRepository: UserPreferencesRepository
    ): GetThemeModeUseCase {
        return GetThemeModeUseCase(userPreferencesRepository)
    }
    
    @Provides
    @Singleton
    fun provideSetThemeModeUseCase(
        userPreferencesRepository: UserPreferencesRepository
    ): SetThemeModeUseCase {
        return SetThemeModeUseCase(userPreferencesRepository)
    }
    
    @Provides
    @Singleton
    fun provideGetDefaultModelUseCase(
        userPreferencesRepository: UserPreferencesRepository
    ): GetDefaultModelUseCase {
        return GetDefaultModelUseCase(userPreferencesRepository)
    }
    
    @Provides
    @Singleton
    fun provideSetDefaultModelUseCase(
        userPreferencesRepository: UserPreferencesRepository
    ): SetDefaultModelUseCase {
        return SetDefaultModelUseCase(userPreferencesRepository)
    }
    
    @Provides
    @Singleton
    fun provideExportConversationUseCase(
        conversationRepository: ConversationRepository
    ): ExportConversationUseCase {
        return ExportConversationUseCase(conversationRepository)
    }
    
    @Provides
    @Singleton
    fun provideShareFileUseCase(
        @ApplicationContext context: Context
    ): ShareFileUseCase {
        return ShareFileUseCase(context)
    }

    @Provides
    @Singleton
    fun provideCreateConversationUseCase(
        conversationRepository: ConversationRepository,
        userPreferencesRepository: UserPreferencesRepository,
        @ApplicationContext context: Context
    ): CreateConversationUseCase {
        return CreateConversationUseCase(conversationRepository, userPreferencesRepository, context)
    }
    
    @Provides
    @Singleton
    fun provideExportAllConversationsUseCase(
        conversationRepository: ConversationRepository,
        exportConversationUseCase: ExportConversationUseCase
    ): ExportAllConversationsUseCase {
        return ExportAllConversationsUseCase(conversationRepository, exportConversationUseCase)
    }
    
    @Provides
    @Singleton
    fun provideDeleteAllDataUseCase(
        conversationRepository: ConversationRepository,
        userPreferencesRepository: UserPreferencesRepository
    ): DeleteAllDataUseCase {
        return DeleteAllDataUseCase(conversationRepository, userPreferencesRepository)
    }
    
    @Provides
    @Singleton
    fun provideManageComparisonModeUseCase(): ManageComparisonModeUseCase {
        return ManageComparisonModeUseCase()
    }
    
    @Provides
    @Singleton
    fun provideSendMessageToMultipleModelsUseCase(
        streamAiResponseUseCase: StreamAiResponseUseCase
    ): SendMessageToMultipleModelsUseCase {
        return SendMessageToMultipleModelsUseCase(streamAiResponseUseCase)
    }
}
