package com.example.letschat.ui

import androidx.lifecycle.ViewModel
import com.example.letschat.repo.IMessagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(repository: IMessagesRepository) : ViewModel() {
    // TODO: Implement the ViewModel
}