package com.example.androidinstrumentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    sealed interface UiState {
        data object Idle : UiState
        data object Loading : UiState
        data class Error(val message: String) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _navigation = MutableSharedFlow<BackendApi.ApiCard>()
    val navigation: SharedFlow<BackendApi.ApiCard> = _navigation.asSharedFlow()

    fun loadFirstApiAndNavigate() {
        if (_uiState.value is UiState.Loading) return
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = runCatching {
                val postId = (1..10).random()
                BackendApi.getUniquePost(postId)
            }
            result.fold(
                onSuccess = { card ->
                    _uiState.value = UiState.Idle
                    _navigation.emit(card)
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Unknown error")
                },
            )
        }
    }
}
