package com.example.chatai.presentation.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatai.domain.usecase.GetTotalUsageStatisticsUseCase
import com.example.chatai.domain.usecase.TotalUsageStatistics
import com.example.chatai.domain.usecase.TotalUsageStatisticsResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsageStatisticsViewModel @Inject constructor(
    private val getTotalUsageStatisticsUseCase: GetTotalUsageStatisticsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsageStatisticsUiState())
    val uiState: StateFlow<UsageStatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            when (val result = getTotalUsageStatisticsUseCase()) {
                is TotalUsageStatisticsResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        statistics = result.statistics,
                        error = null
                    )
                }
                is TotalUsageStatisticsResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class UsageStatisticsUiState(
    val isLoading: Boolean = false,
    val statistics: TotalUsageStatistics? = null,
    val error: String? = null
)

