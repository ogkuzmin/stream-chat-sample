package com.devundefined.streamchatsample.presentation.livedatachat

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.devundefined.streamchatsample.BuildConfig
import com.devundefined.streamchatsample.R
import com.devundefined.streamchatsample.databinding.FragmentChatBinding
import com.devundefined.streamchatsample.domain.UserExtensions.token
import com.devundefined.streamchatsample.domain.UserRepo
import com.devundefined.streamchatsample.presentation.simplechat.MessageAdapter
import com.devundefined.streamchatsample.presentation.simplechat.SimpleChatFragment.Companion.CHANNEL_ID
import com.devundefined.streamchatsample.presentation.simplechat.SimpleChatFragment.Companion.CHANNEL_TYPE
import com.devundefined.streamchatsample.presentation.simplechat.SimpleChatFragment.Companion.MESSAGE_LIMIT
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.errors.ChatError
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.socket.InitConnectionListener
import io.getstream.chat.android.livedata.ChatDomain
import io.getstream.chat.android.livedata.controller.ChannelController
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import timber.log.Timber

class LiveDataChatFragment : Fragment(R.layout.fragment_chat) {

    private lateinit var binding: FragmentChatBinding
    private val appContext: Context by inject()
    private val repo: UserRepo by inject()
    private val currentUser: User by lazy {
        repo.find() ?: throw IllegalStateException("User must be defined")
    }
    private var client: ChatClient? = null
    private lateinit var adapter: MessageAdapter
    private var chatDomain: ChatDomain? = null
    private lateinit var channelController: ChannelController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentChatBinding.bind(view)
        binding.run {
            messageInput.addTextChangedListener { text: Editable? ->
                binding.sendButton.isEnabled = text?.isNotBlank() == true
            }
            messageInput.onFocusChangeListener = View.OnFocusChangeListener { _, _ ->
                messageRecycler.post {
                    messageRecycler.smoothScrollToPosition(adapter.itemCount)
                }
            }
            binding.sendButton.setOnClickListener { sendMessage() }
        }

        client = ChatClient.Builder(BuildConfig.API_KEY, appContext)
            .logLevel(ChatLogLevel.ERROR)
            .build().also {
                it.setUser(currentUser, currentUser.token, object : InitConnectionListener() {
                    override fun onError(error: ChatError) {
                        handleError(error)
                    }

                    override fun onSuccess(data: ConnectionData) {
                        onConnected()
                    }
                })
            }
    }

    private fun sendMessage() {
        binding.run {
            chatDomain?.let { chatDomain ->
                chatDomain.useCases.sendMessage(Message().apply {
                    text = binding.messageInput.text.toString()
                }).enqueue {
                    sendButton.isInvisible = false
                    sendProgress.isVisible = false
                    when (it.isSuccess) {
                        true -> {
                            messageInput.text.clear()
                        }
                        false -> {
                            Toast.makeText(
                                requireContext(),
                                "Failed to send message",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun handleError(error: ChatError) {
        Timber.e(error)
        Toast.makeText(requireContext(), "Error occurred!!!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        runBlocking {
            chatDomain?.disconnect()
        }
        super.onDestroy()
    }

    private fun onConnected() {
        client?.let { chatClient ->
            chatDomain = ChatDomain.Builder(appContext, chatClient, currentUser)
                .offlineEnabled()
                .userPresenceEnabled()
                .build().also { chatDomain ->
                    chatDomain.useCases.watchChannel("$CHANNEL_TYPE:$CHANNEL_ID", MESSAGE_LIMIT).enqueue {
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
                    binding.loader.isVisible = false
                    binding.contentContainer.isVisible = true
                    binding.messageRecycler.layoutManager = LinearLayoutManager(context)
                    binding.messageRecycler.adapter = MessageAdapter(currentUser, messages)
                } else {
                    (binding.messageRecycler.adapter as MessageAdapter).compareAndSet(messages)
                }
            }
        }
    }
}