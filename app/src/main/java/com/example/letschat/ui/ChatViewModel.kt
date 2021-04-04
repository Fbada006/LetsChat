package com.example.letschat.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.letschat.db.Message
import com.example.letschat.repo.IMessagesRepository
import com.example.letschat.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(private val repository: IMessagesRepository) : ViewModel() {

    private val _enableSendButton = MutableStateFlow(false)
    val enableSendButton = _enableSendButton.asLiveData()

    private val _allMessages =
        MutableStateFlow<Resource<List<Message>>>(Resource.Loading())

    val allMessages = _allMessages.asLiveData()

    fun getAllMessages() {
        viewModelScope.launch {
            repository.getAllMessages().onStart {
                _allMessages.value = Resource.Loading()
            }.catch {
                _allMessages.value = Resource.Failure()
            }.collect { messages ->
                if (messages.isEmpty()) {
                    _allMessages.value = Resource.Failure()
                } else {
                    _allMessages.value = Resource.Success(messages)
                }
            }
        }
    }

    fun saveMessage(message: String, isMine: Boolean) {
        viewModelScope.launch {
            repository.insertMessage(Message(text = message, isMine = isMine))
        }
    }

    fun onMessageTextChanged(message: String) {
        _enableSendButton.value = message.isNotEmpty()
    }
}