package com.example.letschat.repo

import com.example.letschat.db.Message
import com.example.letschat.db.MessagesDatabase

class MessagesRepository(
    private val database: MessagesDatabase
) : IMessagesRepository {

    override suspend fun insertMessage(message: Message) {
        database.messageDao.insertMessage(message)
    }

    override fun getAllMessages() = database.messageDao.getAllMessages()
}