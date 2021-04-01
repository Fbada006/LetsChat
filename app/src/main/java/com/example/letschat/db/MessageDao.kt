package com.example.letschat.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessage(message: Message)

    @Query("SELECT * FROM word_table ORDER BY word ASC")
    fun getAllMessages(): Flow<List<Message>>
}