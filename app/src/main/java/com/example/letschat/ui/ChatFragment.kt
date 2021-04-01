package com.example.letschat.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.letschat.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment(R.layout.chat_fragment) {

    private val viewModel: ChatViewModel by viewModels()

}