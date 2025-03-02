package com.data.chatappai.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.chatappai.data.db.Conversation
import com.data.chatappai.data.db.User
import com.data.chatappai.data.repository.ProfileRepository
import com.data.chatappai.domain.model.Profile
import com.data.chatappai.domain.model.Result
import com.data.chatappai.domain.use_case.GetAllConversationsUseCase
import com.data.chatappai.domain.use_case.GetAllUsersUseCase
import com.data.chatappai.domain.use_case.GetMessagesByIdsUseCase
import com.data.chatappai.domain.use_case.InsertConvUseCase
import com.data.chatappai.domain.use_case.InsertUserUseCase
import com.data.chatappai.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val getAllConversationsUseCase: GetAllConversationsUseCase,
    private val insertUserUseCase: InsertUserUseCase,
    private val insertConvUseCase: InsertConvUseCase,
    private val getMessagesByIdsUseCase: GetMessagesByIdsUseCase,
    private val profileRepository: ProfileRepository

    ) : ViewModel() {

    private val _uiUsersState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiUsersState: StateFlow<UiState> = _uiUsersState.asStateFlow()

    private val _uiConversationState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiConversationState: StateFlow<UiState> = _uiConversationState.asStateFlow()

    private val _profileState: MutableStateFlow<Profile> = MutableStateFlow(Profile())
    val profileState = _profileState.asStateFlow()

    init {
        getAllUsers()
        getAllConversations()
        fetchProfile()
    }

    fun getAllUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllUsersUseCase()
                .map { result ->
                    when (result) {
                        is Result.Loading -> UiState.Loading
                        is Result.Success -> UiState.DataLoaded(result.data)
                        is Result.Error -> UiState.Error(result.message)
                    }
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = UiState.Loading
                )
                .onEach { _uiUsersState.value = it } // Updates automatically when Room emits
                .launchIn(viewModelScope)
        }
    }


    private fun getAllConversations() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllConversationsUseCase()
                .map { result ->
                    when (result) {
                        is Result.Loading -> UiState.Loading
                        is Result.Success ->  {
                            UiState.DataLoaded(result.data)
                        }
                        is Result.Error -> UiState.Error(result.message)
                    }
                }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = UiState.Loading
                )
                .onEach { state ->
                    val conversations = (state as? UiState.DataLoaded<List<Conversation>>)?.data ?: emptyList()

                    // Extract last message IDs
                    val lastMsgIds = conversations.mapNotNull { it.lastMessageId }

                    // Fetch messages for those IDs (Assuming it's a suspend function)
                    val messages = getMessagesByIdsUseCase(lastMsgIds).associateBy { it.id }

                    // Update each conversation with its corresponding last message content
                    val updatedConversations = conversations.map { conversation ->
                        val lastMessage = messages[conversation.lastMessageId]
                        conversation.copy(preview = lastMessage?.content ?: "")
                    }

                    // Emit updated state
                    _uiConversationState.value = UiState.DataLoaded(updatedConversations)
                } // Updates automatically when Room emits
                .launchIn(viewModelScope)
        }
    }

    fun addConversation(participants: Set<User>, title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            insertUserUseCase(participants.toList())
            insertConvUseCase(Conversation(title, participants.joinToString(",") { it.id.toString() }))
        }
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            profileRepository.getProfileObject().collect { result ->
                _profileState.value = result
            }
        }
    }
}