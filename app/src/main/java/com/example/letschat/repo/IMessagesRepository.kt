package com.example.letschat.repo

import com.example.letschat.db.Message
import kotlinx.coroutines.flow.Flow

interface IMessagesRepository {
    suspend fun insertMessage(message: Message)
    fun getAllMessages(): Flow<List<Message>>
}