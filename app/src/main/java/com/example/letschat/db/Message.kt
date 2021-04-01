package com.example.letschat.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.letschat.utils.MESSAGE_TABLE_NAME

@Entity(tableName = MESSAGE_TABLE_NAME)
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val time: Long = System.currentTimeMillis(),
    val text: String
)
