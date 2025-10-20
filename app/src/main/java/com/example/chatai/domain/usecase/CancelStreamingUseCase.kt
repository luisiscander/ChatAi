package com.example.chatai.domain.usecase

import javax.inject.Inject

class CancelStreamingUseCase @Inject constructor() {
    private var isStreamingCancelled = false
    
    fun cancelStreaming() {
        isStreamingCancelled = true
    }
    
    fun isCancelled(): Boolean {
        return isStreamingCancelled
    }
    
    fun reset() {
        isStreamingCancelled = false
    }
}
