package com.example.letschat.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.letschat.databinding.ItemMessageBubbleBinding
import com.example.letschat.db.Message

class MessagesAdapter :
    ListAdapter<Message, MessagesAdapter.MessageViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MessageViewHolder constructor(private val binding: ItemMessageBubbleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message) {
            binding.tvMessage.text = item.text
        }

        companion object {
            fun from(parent: ViewGroup): MessageViewHolder {
                val binding =
                    ItemMessageBubbleBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return MessageViewHolder(binding)
            }
        }
    }
}

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}