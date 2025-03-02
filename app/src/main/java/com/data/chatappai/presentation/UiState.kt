package com.data.chatappai.presentation


sealed class UiState {
    data object Loading : UiState()
    data class DataLoaded<T>(val data: T) : UiState()
    data class Error(val message: String) : UiState()
}