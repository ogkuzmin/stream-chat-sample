package com.devundefined.streamchatsample.presentation.chat

import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.devundefined.streamchatsample.databinding.ItemMessageBinding
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.models.name

class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bindData(chatUser: User, message: Message) {
        ItemMessageBinding.bind(itemView).run {
            authorLabel.text = message.user.name
            messageText.text = message.text
            val messageOfCurrentUser = message.user == chatUser

            (authorLabel.layoutParams as LinearLayout.LayoutParams).apply {
                gravity = if (messageOfCurrentUser) Gravity.END else Gravity.START
            }.also {
                authorLabel.layoutParams = it
            }

            (messageText.layoutParams as LinearLayout.LayoutParams).apply {
                gravity = if (messageOfCurrentUser) Gravity.END else Gravity.START
            }.also {
                messageText.layoutParams = it
            }
        }
    }
}