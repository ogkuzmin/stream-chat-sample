package com.devundefined.streamchatsample.presentation.chat

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devundefined.streamchatsample.databinding.ItemMessageBinding
import com.devundefined.streamchatsample.databinding.ItemMessageOwnBinding
import com.devundefined.streamchatsample.domain.UserExtensions.isEqualTo
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.models.name

sealed class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        private const val SMALL_TOP_MARGIN: Float = 4.0F
        private const val STANDARD_TOP_MARGIN: Float = 8.0F
    }

    fun applyMargin(view: View, smallMargin: Boolean) {
        (view.layoutParams as ViewGroup.MarginLayoutParams).apply {
            setMargins(
                leftMargin,
                (if (smallMargin) SMALL_TOP_MARGIN else STANDARD_TOP_MARGIN).toPx(itemView.context),
                rightMargin,
                bottomMargin
            )
        }
    }

    abstract fun bindData(message: Message, messageBefore: Message?)
}

class StandardMessageViewHolder(itemView: View) : MessageViewHolder(itemView) {
    override fun bindData(message: Message, messageBefore: Message?) {
        ItemMessageBinding.bind(itemView).run {
            authorLabel.text = message.user.name
            messageText.text = message.text
            val smallMargin = (messageBefore?.user?.isEqualTo(message.user) == true)
            applyMargin(parent, smallMargin)
        }
    }
}

class OwnMessageViewHolder(itemView: View) : MessageViewHolder(itemView) {
    override fun bindData(message: Message, messageBefore: Message?) {
        ItemMessageOwnBinding.bind(itemView).run {
            authorLabel.text = message.user.name
            messageText.text = message.text
            val smallMargin = (messageBefore?.user?.isEqualTo(message.user) == true)
            applyMargin(parent, smallMargin)
        }
    }
}

fun Float.toPx(context: Context) =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)
        .toInt()