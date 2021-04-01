package com.example.letschat.di

import com.example.letschat.db.MessagesDatabase
import com.example.letschat.repo.IMessagesRepository
import com.example.letschat.repo.MessagesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(database: MessagesDatabase): IMessagesRepository =
        MessagesRepository(database)
}