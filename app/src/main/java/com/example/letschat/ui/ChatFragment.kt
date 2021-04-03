package com.example.letschat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.letschat.R
import com.example.letschat.databinding.ChatFragmentBinding
import com.example.letschat.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChatFragment : Fragment(R.layout.chat_fragment) {

    private val viewModel: ChatViewModel by viewModels()
    private var _binding: ChatFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var messagesAdapter: MessagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ChatFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messagesAdapter = MessagesAdapter()

        binding.fabSend.setOnClickListener {
            val message = binding.etMessage.text.toString()
            clearTextAndHideKeyboard()
            val isMine = listOf(true, false).random()
            viewModel.saveMessage(message, isMine)
        }

        binding.etMessage.addTextChangedListener {
            viewModel.onMessageTextChanged(it.toString())
        }

        viewModel.enableSendButton.observe(viewLifecycleOwner, {
            binding.fabSend.isEnabled = it
        })

        initRecyclerView()
    }

    private fun clearTextAndHideKeyboard() {
        binding.etMessage.text?.clear()
        binding.etMessage.clearFocus()
        hideKeyboard()
    }

    private fun initRecyclerView() {
        binding.rvMessages.apply {
            adapter = messagesAdapter
            itemAnimator = DefaultItemAnimator()
        }

        viewModel.allMessages.observe(viewLifecycleOwner, {
            messagesAdapter.submitList(it)
            val bottomOfList = it.size - 1
            binding.rvMessages.postDelayed({
                binding.rvMessages.scrollToPosition(bottomOfList)
            }, 500)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}