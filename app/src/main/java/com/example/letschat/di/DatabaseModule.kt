package com.example.letschat.di

import android.content.Context
import androidx.room.Room
import com.example.letschat.db.MessagesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): MessagesDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            MessagesDatabase::class.java,
            "messages_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}