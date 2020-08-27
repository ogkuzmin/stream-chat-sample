package com.devundefined.streamchatsample.presentation.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devundefined.streamchatsample.R
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.models.User

class MessageAdapter(
    private val currentUser: User,
    initialMessages: List<Message>
) : RecyclerView.Adapter<MessageViewHolder>() {

    private var messages: List<Message> = initialMessages

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
            .let(::MessageViewHolder)

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) = holder.bindData(
        currentUser,
        messages[position]
    )

    fun insertNew(message: Message) {
        messages = messages + message
        notifyItemInserted(messages.lastIndex)
    }
}