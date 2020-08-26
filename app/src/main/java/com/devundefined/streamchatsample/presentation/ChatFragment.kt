package com.devundefined.streamchatsample.presentation

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.devundefined.streamchatsample.BuildConfig
import com.devundefined.streamchatsample.R
import com.devundefined.streamchatsample.databinding.FragmentChatBinding
import com.devundefined.streamchatsample.domain.UserExtensions.token
import com.devundefined.streamchatsample.domain.UserRepo
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelRequest
import io.getstream.chat.android.client.controllers.ChannelController
import io.getstream.chat.android.client.errors.ChatError
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.models.name
import io.getstream.chat.android.client.socket.InitConnectionListener
import org.koin.android.ext.android.inject
import timber.log.Timber

class ChatFragment : Fragment(R.layout.fragment_chat) {
    private val userRepo: UserRepo by inject()
    private val appContext: Context by inject()
    private lateinit var client: ChatClient
    private lateinit var channelController: ChannelController
    private lateinit var binding: FragmentChatBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatBinding.bind(view)

        client = ChatClient.Builder(BuildConfig.API_KEY, appContext)
            .logLevel(ChatLogLevel.ALL)
            .build()
        val user = userRepo.find() ?: throw IllegalArgumentException("User must be non null!!!")
        client.setUser(user, user.token, object : InitConnectionListener() {
            override fun onError(error: ChatError) {
                handleError(error)
            }

            override fun onSuccess(data: ConnectionData) {
                onConnected()
            }
        })
        binding.messageInput.addTextChangedListener { text: Editable? ->
            binding.sendButton.isEnabled = text?.isNotBlank() == true
        }
        binding.sendButton.setOnClickListener { sendMessage() }
    }

    private fun sendMessage() {
        binding.run {
            sendButton.isInvisible = true
            sendProgress.isVisible = true
            channelController.sendMessage(Message().apply { text = messageInput.text.toString() })
                .enqueue {
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

    private fun onConnected() {
        channelController = client.channel(CHANNEL_TYPE, CHANNEL_ID)
        val data: Map<String, Any> = mapOf()
        channelController.query(
            QueryChannelRequest()
                .withData(data)
                .withMessages(100)
                .withWatch()
        ).enqueue {
            when (it.isSuccess) {
                true -> showChannelContent(it.data())
                false -> handleError(it.error())
            }
        }
    }

    private fun showChannelContent(channel: Channel) {
        binding.loader.isVisible = false
        binding.contentContainer.isVisible = true
        showMessages(channel.messages)
        channel.name.takeIf(String::isNotBlank)?.let { title ->
            binding.toolbar.title = title
        }
    }

    private fun handleError(error: ChatError) {
        Timber.e(error)
        Toast.makeText(requireContext(), "Error occurred!!!", Toast.LENGTH_SHORT).show()
    }

    private fun showMessages(messages: List<Message>) {
        messages.size
    }


    companion object {
        private const val CHANNEL_TYPE = "livestream"
        private const val CHANNEL_ID = "channel_id"
        private const val KEY_NAME = "name"
        private const val CHANNEL_NAME = "Sample channel"
    }
}