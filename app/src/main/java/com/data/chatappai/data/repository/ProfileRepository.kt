package com.data.chatappai.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.data.chatappai.domain.model.Profile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton


private val Context.dataStorePreference: DataStore<Preferences> by preferencesDataStore(name = "profile_prefs")
val Context.dataStore by dataStore("app-settings.json", ProfileSerializer)

@Singleton
class ProfileRepository @Inject constructor(@ApplicationContext context: Context) {

    private val dataStorePreference = context.dataStorePreference
    private val dataStore = context.dataStore

    companion object {
        private val NAME_KEY = stringPreferencesKey("profile_name")
        private val PHOTO_KEY = stringPreferencesKey("profile_photo")
    }

    // Flow to observe profile updates
    val profileFlow: Flow<Profile> = dataStorePreference.data.map { preferences ->
        Profile(
            name = preferences[NAME_KEY] ?: "",
            avatar = preferences[PHOTO_KEY] ?: ""
        )
    }


    // Save profile
    suspend fun saveProfile(name: String, photoUri: String) {
        dataStorePreference.edit { preferences ->
            preferences[NAME_KEY] = name
            preferences[PHOTO_KEY] = photoUri
        }
    }

    // Get profile once (not as flow)
    suspend fun getProfile(): Profile {
        return dataStorePreference.data.first().let { preferences ->
            Profile(
                name = preferences[NAME_KEY] ?: "",
                avatar = preferences[PHOTO_KEY] ?: ""
            )
        }
    }    // Save profile

    suspend fun saveProfileObject(name: String, photoUri: String) {
        dataStore.updateData {
            it.copy(
                name = name,
                avatar = photoUri
            )

        }
    }

    // Get profile once (not as flow)
    fun getProfileObject(): Flow<Profile> {
        return dataStore.data
    }

}

object ProfileSerializer : Serializer<Profile> {

    override val defaultValue: Profile
        get() = Profile()

    override suspend fun readFrom(input: InputStream): Profile {
        return try {
            Json.decodeFromString(
                deserializer = Profile.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: Profile, output: OutputStream) {
        output.write(
            Json.encodeToString(
                serializer = Profile.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}
