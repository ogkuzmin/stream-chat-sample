package com.devundefined.streamchatsample.presentation.chat.livedatachat

import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.devundefined.streamchatsample.presentation.chat.BaseChatFragment
import com.devundefined.streamchatsample.presentation.chat.MessageAdapter
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.livedata.ChatDomain
import io.getstream.chat.android.livedata.controller.ChannelController
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LiveDataChatFragment : BaseChatFragment() {
    private var chatDomain: ChatDomain? = null
    private lateinit var channelController: ChannelController

    override fun sendMessage() {
        binding.run {
            onMessageSending()
            chatDomain?.let { chatDomain ->
                chatDomain.useCases.sendMessage(getMessage()).enqueue {
                    lifecycleScope.launch {
                        when (it.isSuccess) {
                            true -> onMessageSendSuccess()
                            false -> onMessageSendFailed()
                        }
                    }
                }
            }
        }
    }

    override fun getMessage(): Message {
        return super.getMessage().apply { channel = Channel(getCid()) }
    }

    override fun disposeResources() {
        runBlocking {
            chatDomain?.disconnect()
        }
    }

    private fun getCid() = "$CHANNEL_TYPE:$CHANNEL_ID"

    override fun onConnected() {
        client?.let { chatClient ->
            chatDomain = ChatDomain.Builder(appContext, chatClient, currentUser)
                .offlineEnabled()
                .userPresenceEnabled()
                .build().also { chatDomain ->
                    chatDomain.useCases.watchChannel(getCid(), MESSAGE_LIMIT)
                        .enqueue {
                            when (it.isSuccess) {
                                true -> {
                                    channelController = it.data()
                                    showChannelContent(it.data().messages)
                                }
                                false -> handleError(it.error())
                            }
                        }
                }
        }
    }

    private fun showChannelContent(messagesSource: LiveData<List<Message>>) {
        binding.messageRecycler.handler.post {
            messagesSource.observe(this) { messages ->
                if (binding.messageRecycler.adapter == null) {
                    showMessages(messages)
                } else {
                    binding.messageRecycler.run {
                        (adapter as MessageAdapter).compareAndSet(messages)
                        scrollToLast()
                    }
                }
            }
        }
    }
}