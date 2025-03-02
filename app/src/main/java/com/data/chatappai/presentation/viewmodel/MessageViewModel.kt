package com.data.chatappai.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.chatappai.data.db.Conversation
import com.data.chatappai.data.db.User
import com.data.chatappai.data.repository.ProfileRepository
import com.data.chatappai.domain.model.Profile
import com.data.chatappai.domain.model.Result
import com.data.chatappai.domain.use_case.GetAIResponseUseCase
import com.data.chatappai.domain.use_case.GetAllMessagesUseCase
import com.data.chatappai.domain.use_case.GetConversationByIdUseCase
import com.data.chatappai.domain.use_case.GetUsersByIdUseCase
import com.data.chatappai.domain.use_case.InsertMessagesUseCase
import com.data.chatappai.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val insertMessagesUseCase: InsertMessagesUseCase,
    private val getAllMessagesUseCase: GetAllMessagesUseCase,
    private val getConversationByIdUseCase: GetConversationByIdUseCase,
    private val getAIResponseUseCase: GetAIResponseUseCase,
    private val getUsersByIdUseCase: GetUsersByIdUseCase,
    private val profileRepository: ProfileRepository,
) : ViewModel() {
    val convId: Int = savedStateHandle["convId"] ?: 0
    private val _convState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val conversationState = _convState.asStateFlow()

    private val _allMessagesState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val allMessagesState = _allMessagesState.asStateFlow()

    private val _allUsers: MutableStateFlow<Map<Long, User>> = MutableStateFlow(emptyMap())
    val allUsers = _allUsers.asStateFlow()
    private val _profileState: MutableStateFlow<Profile> = MutableStateFlow(Profile())
    val profileState = _profileState.asStateFlow()
    init {
        viewModelScope.launch(Dispatchers.IO) {
            fetchMessages()
            fetchConversation()
            fetchProfile()

        }

    }

    private fun fetchProfile() = viewModelScope.launch(Dispatchers.IO) {
        profileRepository.getProfileObject().collect { result ->
            _profileState.value = result
        }
    }

    fun sendMessage(text: String) {
        if (text.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                val job = launch {
                    insertMessagesUseCase("me", text, convId)
                }
                job.join()
                launch {
                    if (_allMessagesState.value is UiState.DataLoaded<*>) {
                        getAIResponseUseCase(convId)
                    }
                }
            }
        }

    }

    private suspend fun fetchMessages() {
        getAllMessagesUseCase(convId).map { result ->
            when (result) {
                is Result.Error -> {
                    UiState.Error(result.message)
                }

                Result.Loading -> {
                    UiState.Loading
                }

                is Result.Success -> {
                    UiState.DataLoaded(result.data)
                }
            }

        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        ).onEach {
                _allMessagesState.value = it
            } // Updates automatically when Room emits
            .launchIn(viewModelScope)
    }

    private suspend fun fetchConversation() {
        getConversationByIdUseCase(convId).map { result ->
            when (result) {
                is Result.Error -> {
                    UiState.Error(result.message)
                }

                Result.Loading -> {
                    UiState.Loading
                }

                is Result.Success -> {
                    val conversation = result.data

                    // Launch a coroutine to fetch users after fetching the conversation
                    viewModelScope.launch {
                        fetchUsers(conversation)
                    }
                    UiState.DataLoaded(conversation)
                }
            }

        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        ).onEach {
                _convState.value = it
            } // Updates automatically when Room emits
            .launchIn(viewModelScope)
    }
    private suspend fun fetchUsers(conversation: Conversation) {
        try {
            // Fetch users by their participant IDs in parallel
            getUsersByIdUseCase(conversation.participants.split(",").map {
                it.toLong()
            }).collect { users ->
                _allUsers.value = users.associateBy { it.id }
            }

        } catch (e: Exception) {
            // Handle any error that may occur during the user fetching process
            _convState.value = UiState.Error("Error fetching users: ${e.message}")
        }
    }
}