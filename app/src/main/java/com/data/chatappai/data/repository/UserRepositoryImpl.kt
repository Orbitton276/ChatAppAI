package com.data.chatappai.data.repository

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import com.data.chatappai.R

import com.data.chatappai.data.db.User
import com.data.chatappai.data.db.UserDao
import com.data.chatappai.domain.repository.UserRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDao: UserDao,
) : UserRepository {
    override suspend fun getAllUsers(): Flow<List<User>> {

        return flow {
            try {
                emit(fetchContacts(context))
            } catch (e: Exception) {
                emit(emptyList())
            }
        }
    }
    private fun fetchContacts(context: Context): List<User> {
        val contactsList = mutableListOf<User>()

        val contentResolver = context.contentResolver
        val uri = ContactsContract.Contacts.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_URI
        )

        val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            val idIndex = it.getColumnIndex(ContactsContract.Contacts._ID)
            val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
            val photoIndex = it.getColumnIndex(ContactsContract.Contacts.PHOTO_URI)

            while (it.moveToNext()) {
                val contactId = it.getLong(idIndex)
                val name = it.getString(nameIndex) ?: context.getString(R.string.unknown)
                val photoUri = it.getString(photoIndex) // Can be null if no photo
                if (!photoUri.isNullOrBlank()) {
                    contactsList.add(User(name, photoUri, contactId))
                }
            }
        }

        cursor?.close()
        return contactsList
    }

    override suspend fun getUserById(userId: Int): Flow<User> {
        return userDao.getUserById(userId)
    }

    override suspend fun insertUser(users: List<User>) {
        userDao.addUsers(users)
    }

    override suspend fun getUsersByIds(userIds: List<Long>) : Flow<List<User>> {
        return userDao.getUsersByIds(userIds)
    }

}