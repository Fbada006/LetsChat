package com.example.letschat.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.letschat.databinding.ChatFragmentBinding
import com.example.letschat.utils.AblyConnection
import com.example.letschat.utils.AblyConnectionCallback
import com.example.letschat.utils.Resource
import com.example.letschat.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import io.ably.lib.realtime.Channel
import io.ably.lib.types.AblyException
import io.ably.lib.types.Message
import timber.log.Timber


@AndroidEntryPoint
class ChatFragment : Fragment() {

    private val choices = arrayOf(true, false)
    private val viewModel: ChatViewModel by viewModels()
    private var _binding: ChatFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var messagesAdapter: MessagesAdapter
    private val messageListener: Channel.MessageListener =
        Channel.MessageListener { message: Message ->
            viewModel.saveMessage(
                message.data.toString(),
                choices.random()
            )
        }
    private val connectionCallback = object : AblyConnectionCallback {
        override fun onConnectionCallback(exception: Exception?) {
            try {
                if (exception == null) {
                    AblyConnection.initListener(messageListener)
                } else {
                    Timber.e("Error initialising listener with a non null exception ", exception)
                }
            } catch (exception: AblyException) {
                Timber.e("Error initialising listener ", exception)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        AblyConnection.establishConnectionForID("userNameHere", connectionCallback)
        _binding = ChatFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messagesAdapter = MessagesAdapter()
        viewModel.getAllMessages()

        binding.fabSend.setOnClickListener {
            try {
                val message = binding.etMessage.text.toString()
                AblyConnection.sendMessage(message, object : AblyConnectionCallback {
                    override fun onConnectionCallback(exception: Exception?) {
                        if (exception != null) {
                            Timber.e("Error sending message", exception)
                        }
                        activity?.runOnUiThread { clearTextAndHideKeyboard() }
                    }
                })
            } catch (exception: AblyException) {
                Timber.e("Error in clicking send button", exception)
            }
        }

        binding.etMessage.addTextChangedListener {
            viewModel.onMessageTextChanged(it.toString())
        }

        viewModel.enableSendButton.observe(viewLifecycleOwner) {
            binding.fabSend.isEnabled = it
        }

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

        viewModel.allMessages.observe(viewLifecycleOwner) { messageRes ->
            when (messageRes) {
                is Resource.Failure -> {
                    showFailure()
                }
                is Resource.Loading -> {
                    showLoading()
                }
                is Resource.Success -> {
                    showSuccess()
                    messagesAdapter.submitList(messageRes.data)
                    val bottomOfList = messageRes.data.size - 1
                    binding.rvMessages.postDelayed({
                        binding.rvMessages.scrollToPosition(bottomOfList)
                    }, 500)
                }
            }
        }
    }

    private fun showFailure() {
        binding.rvMessages.isVisible = false
        binding.pbLoading.isVisible = false
        binding.tvError.isVisible = true
    }

    private fun showLoading() {
        binding.rvMessages.isVisible = false
        binding.pbLoading.isVisible = true
        binding.tvError.isVisible = false
    }

    private fun showSuccess() {
        binding.rvMessages.isVisible = true
        binding.pbLoading.isVisible = false
        binding.tvError.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}