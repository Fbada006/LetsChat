package com.example.letschat.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.letschat.R
import com.example.letschat.databinding.ChatFragmentBinding
import com.example.letschat.utils.Resource
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
        viewModel.getAllMessages()

        binding.fabSend.setOnClickListener {
            val slideOutBottom: Animation = AnimationUtils.loadAnimation(
                context,
                R.anim.out_bottom
            )
            slideOutBottom.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    displaySendChoice()
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }
            })
            binding.layoutMessageInput.startAnimation(slideOutBottom)
        }

        binding.etMessage.addTextChangedListener {
            viewModel.onMessageTextChanged(it.toString())
        }

        viewModel.enableSendButton.observe(viewLifecycleOwner, {
            binding.fabSend.isEnabled = it
        })

        initRecyclerView()
    }

    private fun displaySendChoice() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.send_as))

        val choices =
            arrayOf(getString(R.string.send_as_me_label), getString(R.string.send_as_not_me_label))
        builder.setItems(choices) { dialog, which ->
            dialog.dismiss()
            val message = binding.etMessage.text.toString()
            clearTextAndHideKeyboard()
            val isMine = choices[which] == getString(R.string.send_as_me_label)
            viewModel.saveMessage(message, isMine)
        }

        val dialog = builder.create()
        dialog.show()
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

        viewModel.allMessages.observe(viewLifecycleOwner, { messageRes ->
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
        })
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