package com.data.chatappai.data.mapper

import com.data.chatappai.data.db.MessageEntity
import com.data.chatappai.domain.model.Message

fun MessageEntity.toDomainModel() : Message {
    return  Message(
        senderId = senderId,
        content = content,
        timestamp = timestamp,
        convId = convId,
        id = id,
        status = status,
    )
}