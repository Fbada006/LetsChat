package com.example.letschat.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Message::class], version = 1)
abstract class MessagesDatabase : RoomDatabase() {

    abstract val messageDao: MessageDao
}