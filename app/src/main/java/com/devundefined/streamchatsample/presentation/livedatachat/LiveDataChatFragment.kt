package com.devundefined.streamchatsample.presentation.livedatachat

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.devundefined.streamchatsample.BuildConfig
import com.devundefined.streamchatsample.R
import com.devundefined.streamchatsample.databinding.FragmentChatBinding
import com.devundefined.streamchatsample.domain.UserExtensions.token
import com.devundefined.streamchatsample.domain.UserRepo
import com.devundefined.streamchatsample.presentation.simplechat.MessageAdapter
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.errors.ChatError
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.socket.InitConnectionListener
import io.getstream.chat.android.client.utils.observable.Subscription
import io.getstream.chat.android.livedata.ChatDomain
import org.koin.android.ext.android.inject

class LiveDataChatFragment : Fragment(R.layout.fragment_chat) {

    private lateinit var binding: FragmentChatBinding
    private val appContext: Context by inject()
    private val repo: UserRepo by inject()
    private val currentUser: User by lazy {
        repo.find() ?: throw IllegalStateException("User must be defined")
    }
    private var client: ChatClient? = null
    private lateinit var adapter: MessageAdapter
    private var subscription: Subscription? = null
    private var chatDomain: ChatDomain? = null

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

        ChatClient.Builder(BuildConfig.API_KEY, appContext)
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
        TODO("Not yet implemented")
    }

    private fun handleError(error: ChatError) {
        TODO("Not yet implemented")
    }

    private fun onConnected() {
        client?.let { chatClient ->
            chatDomain = ChatDomain.Builder(appContext, chatClient, currentUser)
                .offlineEnabled()
                .userPresenceEnabled()
                .build()
        }
    }
}