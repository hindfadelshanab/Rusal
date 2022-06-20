package com.mobileq.rusal.rusalapp.developer3456.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobileq.rusal.rusalapp.developer3456.databinding.ItemContienerReceivedMessageBinding
import com.mobileq.rusal.rusalapp.developer3456.databinding.ItemContinerSendMessageBinding
import com.mobileq.rusal.rusalapp.developer3456.model.Message
import com.squareup.picasso.Picasso

//MessageAdpter

class MessageAdpter(
    chatMessageList: List<Message>,
    senderId: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val chatMessageList: List<Message>
    private val senderId: String



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            SentMessageViewHolder(
                ItemContinerSendMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            ReceivedMessageViewHolder(
                ItemContienerReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            (holder as SentMessageViewHolder).setData(chatMessageList[position])
        } else {
            (holder as ReceivedMessageViewHolder).setData(
                chatMessageList[position]
            )
        }
    }

    override fun getItemCount(): Int {
        return chatMessageList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (chatMessageList[position].senderId.equals(senderId)) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    internal class SentMessageViewHolder(itemContinerSendMessageBinding: ItemContinerSendMessageBinding) :
        RecyclerView.ViewHolder(itemContinerSendMessageBinding.getRoot()) {
        private val binding: ItemContinerSendMessageBinding
        fun setData(chatMessage: Message) {
            binding.textMessage.setText(chatMessage.message)
            binding.textDateTime.setText(chatMessage.dateTime)
            if (chatMessage.messageImage != null) {
                if (!chatMessage.messageImage.equals("")) {
                    binding.messageImage.setVisibility(View.VISIBLE)
                    Picasso.get().load(chatMessage.messageImage).into(binding.messageImage)
                }
            }

        }

        init {
            binding = itemContinerSendMessageBinding
        }
    }

    internal class ReceivedMessageViewHolder(itemContienerReceivedMessageBinding: ItemContienerReceivedMessageBinding) :
        RecyclerView.ViewHolder(itemContienerReceivedMessageBinding.getRoot()) {
        private val binding: ItemContienerReceivedMessageBinding
        fun setData(chatMessage: Message) {
            binding.textMessage.setText(chatMessage.message)
            binding.textDateTime.setText(chatMessage.dateTime)

            if (chatMessage.users !=null) {

                for (u in chatMessage.users!!) {
                    //if (chatMessage.receiverId)
                    binding.txtReceivedName.setText(u.name);
                }
            }
            if (chatMessage.messageImage != null) {
                if (!chatMessage.messageImage.equals("")) {
                    binding.messageImage.setVisibility(View.VISIBLE)
                    Picasso.get().load(chatMessage.messageImage).into(binding.messageImage)
                }
            }


            if (chatMessage.senderImage != null) {

                Picasso.get()
                    .load(chatMessage.senderImage)
                    .into(binding.imageProfile)
            }
        }

        init {
            binding = itemContienerReceivedMessageBinding
        }
    }

    companion object {
        const val VIEW_TYPE_SENT = 1
        const val VIEW_TYPE_RECEIVED = 2
    }

    init {
        this.chatMessageList = chatMessageList
        this.senderId = senderId
    }
}
