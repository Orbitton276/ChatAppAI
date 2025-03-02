package com.data.chatappai.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val name: String = "",
    val avatar: String = "",
) {
    fun isEmpty() : Boolean{
        return name.isBlank() && avatar.isBlank()
    }
}