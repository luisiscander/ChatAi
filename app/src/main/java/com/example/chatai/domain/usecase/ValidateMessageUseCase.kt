package com.example.chatai.domain.usecase

import javax.inject.Inject

class ValidateMessageUseCase @Inject constructor() {
    
    companion object {
        const val MAX_MESSAGE_LENGTH = 10_000
    }
    
    operator fun invoke(message: String): MessageValidationResult {
        return when {
            message.isBlank() -> MessageValidationResult.Empty
            message.length > MAX_MESSAGE_LENGTH -> MessageValidationResult.TooLong
            else -> MessageValidationResult.Valid
        }
    }
}

sealed class MessageValidationResult {
    object Empty : MessageValidationResult()
    object TooLong : MessageValidationResult()
    object Valid : MessageValidationResult()
}
