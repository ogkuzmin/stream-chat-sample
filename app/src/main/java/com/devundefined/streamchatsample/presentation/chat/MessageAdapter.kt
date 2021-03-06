package com.devundefined.streamchatsample.presentation.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.devundefined.streamchatsample.R
import com.devundefined.streamchatsample.domain.UserExtensions.isEqualTo
import com.devundefined.streamchatsample.presentation.chat.simplechat.MessageViewHolder
import com.devundefined.streamchatsample.presentation.chat.simplechat.OwnMessageViewHolder
import com.devundefined.streamchatsample.presentation.chat.simplechat.StandardMessageViewHolder
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

    fun compareAndSet(newMessages: List<Message>) {
        MessageDiffUtil(messages, newMessages)
            .let(DiffUtil::calculateDiff)
            .dispatchUpdatesTo(this)
        messages = newMessages
    }

    private class MessageDiffUtil(
        private val oldList: List<Message>,
        private val list: List<Message>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = list.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldValue = oldList[oldItemPosition]
            val newValue = list[newItemPosition]
            return oldValue.id == newValue.id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldValue = oldList[oldItemPosition]
            val newValue = list[newItemPosition]
            return oldValue.id == newValue.id && oldValue.text == newValue.text
        }
    }

    companion object {
        private const val VIEW_TYPE_STANDARD = 1
        private const val VIEW_TYPE_OWN_MESSAGE = 2
    }
}