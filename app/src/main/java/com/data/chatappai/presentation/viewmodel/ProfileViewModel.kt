package com.data.chatappai.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.data.chatappai.data.repository.ProfileRepository
import com.data.chatappai.domain.model.Profile
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    @ApplicationContext private val context: Context,
    @Singleton private val applicationScope: CoroutineScope
) : ViewModel() {

    private val _profileState = MutableStateFlow(Profile()) // Initial state with an empty Profile
    val profileState: StateFlow<Profile> = _profileState

    private val _hasProfile = MutableStateFlow<Boolean?>(null) // Initially null
    val hasProfile: StateFlow<Boolean?> = _hasProfile

    init {
        viewModelScope.launch {
            profileRepository.getProfileObject().collect { profile ->
                _hasProfile.value = !profile.isEmpty() // Set true if profile exists, false otherwise
                _profileState.value = profile
            }
        }
    }

    // Function to save or update the profile
    fun saveProfile(name: String, photoUri: String) {
        viewModelScope.launch {
            profileRepository.saveProfile(name, photoUri)
            _hasProfile.value = true
        }
    }

    // Function to save profile using the object-based approach (with Profile object)
    fun saveProfileObject(name: String, photoUri: Uri?) {
        applicationScope.launch {
            val copyTempToPermanent = photoUri?.takeIf { it.toString().isNotBlank() }?.let {
                copyImageToInternalStorage(context, it)
            } ?: ""
            profileRepository.saveProfileObject(name, copyTempToPermanent.toString())
            _hasProfile.value = true
        }
    }

    // Function to copy the image to internal storage and return the new URI
    private fun copyImageToInternalStorage(context: Context, uri: Uri): Uri {
        val contentResolver = context.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
        val imageFile = File(context.filesDir, "profile_avatar_${System.currentTimeMillis()}.jpg")

        val outputStream = FileOutputStream(imageFile)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        return Uri.fromFile(imageFile)
    }

    // Function to get the profile data once (non-flow)
    suspend fun getProfile(): Profile {
        return profileRepository.getProfile()
    }

    suspend fun getProfileObject(): Profile {
        return profileRepository.getProfile()
    }
}
