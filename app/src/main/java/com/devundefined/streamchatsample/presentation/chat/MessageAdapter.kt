package com.devundefined.streamchatsample.presentation.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devundefined.streamchatsample.R
import com.devundefined.streamchatsample.domain.UserExtensions.isEqualTo
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.models.User

class MessageAdapter(
    private val currentUser: User,
    initialMessages: List<Message>
) : RecyclerView.Adapter<MessageViewHolder>() {

    private var messages: List<Message> = initialMessages

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder =
        when (viewType) {
            VIEW_TYPE_OWN_MESSAGE -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_own, parent, false)
                .let(::OwnMessageViewHolder)
            VIEW_TYPE_STANDARD -> LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message, parent, false)
                .let(::StandardMessageViewHolder)
            else -> throw IllegalStateException("Doesn't matched to expected constants")
        }

    override fun getItemViewType(position: Int): Int =
        if (messages[position].user.isEqualTo(currentUser)) {
            VIEW_TYPE_OWN_MESSAGE
        } else {
            VIEW_TYPE_STANDARD
        }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val messageBefore = if (position > 0) {
            messages[position - 1]
        } else {
            null
        }
        holder.bindData(messages[position], messageBefore)
    }


    fun insertNew(message: Message) {
        messages = messages + message
        notifyItemInserted(messages.lastIndex)
    }

    companion object {
        private const val VIEW_TYPE_STANDARD = 1
        private const val VIEW_TYPE_OWN_MESSAGE = 2
    }
}