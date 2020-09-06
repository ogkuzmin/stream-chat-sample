package com.devundefined.streamchatsample.presentation.chat.simplechat

import com.devundefined.streamchatsample.presentation.MainActivity
import com.devundefined.streamchatsample.presentation.chat.BaseChatFragment
import com.devundefined.streamchatsample.presentation.chat.MessageAdapter
import io.getstream.chat.android.client.api.models.QueryChannelRequest
import io.getstream.chat.android.client.controllers.ChannelController
import io.getstream.chat.android.client.events.NewMessageEvent
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.models.name
import io.getstream.chat.android.client.utils.observable.Subscription
import timber.log.Timber

class SimpleChatFragment : BaseChatFragment() {
    private var channelController: ChannelController? = null
    private lateinit var adapter: MessageAdapter
    private var subscription: Subscription? = null

    override fun sendMessage() {
        binding.run {
            onMessageSending()
            channelController?.sendMessage(getMessage())?.enqueue {
                when (it.isSuccess) {
                    true -> onMessageSendSuccess()
                    false -> onMessageSendFailed()
                }
            }
        }
    }

    override fun onConnected() {
        channelController = client?.channel(CHANNEL_TYPE, CHANNEL_ID)
        channelController?.query(
            QueryChannelRequest().withData(mapOf()).withMessages(MESSAGE_LIMIT).withWatch()
        )?.enqueue {
            when (it.isSuccess) {
                true -> showChannelContent(it.data())
                false -> handleError(it.error())
            }
        }
        channelController?.events()?.filter(EVENT_TYPE_NEW_MESSAGES)?.subscribe { chatEvent ->
            (chatEvent as? NewMessageEvent)?.let { onNewMessage(it.message) }
        }
    }

    override fun disposeResources() {
        channelController?.stopWatching()
        subscription?.unsubscribe()
    }

    private fun onNewMessage(message: Message) {
        Timber.d("Received new message:\n${message.text}")
        adapter.insertNew(message)
        binding.messageRecycler.smoothScrollToPosition(adapter.itemCount)
    }

    private fun showChannelContent(channel: Channel) {
        showMessages(channel.messages)
        channel.name.takeIf(String::isNotBlank)
            ?.let { (activity as? MainActivity)?.updateToolbarTitle(it) }
    }

    companion object {
        private const val EVENT_TYPE_NEW_MESSAGES = "message.new"
    }
}