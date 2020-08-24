package com.devundefined.streamchatsample.presentation

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.devundefined.streamchatsample.BuildConfig
import com.devundefined.streamchatsample.R
import com.devundefined.streamchatsample.domain.UserExtensions.token
import com.devundefined.streamchatsample.domain.UserRepo
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.api.models.QueryChannelRequest
import io.getstream.chat.android.client.errors.ChatError
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.socket.InitConnectionListener
import org.koin.android.ext.android.inject
import timber.log.Timber

class ChatFragment : Fragment(R.layout.fragment_chat) {
    private val userRepo: UserRepo by inject()
    private val appContext: Context by inject()
    private lateinit var client: ChatClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        client = ChatClient.Builder(BuildConfig.API_KEY, appContext)
            .logLevel(ChatLogLevel.ALL)
            .build()
        val user = userRepo.find() ?: throw IllegalArgumentException("User must be non null!!!")
        client.setUser(user, user.token, object : InitConnectionListener() {
            override fun onError(error: ChatError) {
                handleError(error)
            }

            override fun onSuccess(data: ConnectionData) {
                onСonnected()
            }
        })
    }

    private fun onСonnected() {
        val channelController = client.channel(CHANNEL_TYPE, CHANNEL_ID)
        val data: Map<String, Any> = mapOf()
        channelController.query(QueryChannelRequest()
            .withData(data)
            .withMessages(100)
            .withWatch()
        ).enqueue {
            when(it.isSuccess) {
                true -> {
                    val channel = it.data()
                    showMessages(channel.messages)
                }
                false -> {
                    handleError(it.error())
                }
            }
        }
    }

    private fun handleError(error: ChatError) {
        Timber.e(error)
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