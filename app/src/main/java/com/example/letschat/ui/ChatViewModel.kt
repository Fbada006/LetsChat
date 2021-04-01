package com.example.letschat.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.letschat.db.Message
import com.example.letschat.repo.IMessagesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val repository: IMessagesRepository) : ViewModel() {

    private val _enableSendButton = MutableStateFlow(false)
    val enableSendButton = _enableSendButton.asLiveData()

    fun saveMessage(message: String) {
        viewModelScope.launch {
            repository.insertMessage(Message(text = message))
        }
    }

    fun onMessageTextChanged(message: String) {
        _enableSendButton.value = message.isNotEmpty()
    }
}