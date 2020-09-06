package com.devundefined.streamchatsample.presentation.chat

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.devundefined.streamchatsample.BuildConfig
import com.devundefined.streamchatsample.R
import com.devundefined.streamchatsample.databinding.FragmentChatBinding
import com.devundefined.streamchatsample.domain.UserExtensions.token
import com.devundefined.streamchatsample.domain.UserRepo
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.errors.ChatError
import io.getstream.chat.android.client.logger.ChatLogLevel
import io.getstream.chat.android.client.models.Message
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.socket.InitConnectionListener
import org.koin.android.ext.android.inject
import timber.log.Timber

abstract class BaseChatFragment : Fragment(R.layout.fragment_chat) {
    private val userRepo: UserRepo by inject()
    protected val appContext: Context by inject()
    protected lateinit var currentUser: User
    protected lateinit var binding: FragmentChatBinding
    protected var client: ChatClient? = null

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatBinding.bind(view)
        currentUser = userRepo.find() ?: throw IllegalArgumentException("User must be non null!!!")

        binding.run {
            messageInput.addTextChangedListener { text: Editable? ->
                binding.sendButton.isEnabled = text?.isNotBlank() == true
            }
            binding.sendButton.setOnClickListener { sendMessage() }
        }
        initChat()
    }

    @CallSuper
    override fun onDestroy() {
        disposeResources()
        super.onDestroy()
    }

    private fun initChat() {
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

    abstract fun onConnected()
    abstract fun sendMessage()
    abstract fun disposeResources()

    protected fun showMessages(messages: List<Message>) {
        binding.loader.isVisible = false
        binding.contentContainer.isVisible = true
        binding.messageInput.requestFocus()
        binding.messageRecycler.run {
            adapter = MessageAdapter(currentUser, messages)
            layoutManager = LinearLayoutManager(context)
            post { scrollToLast() }
        }
    }

    protected fun onMessageSending() {
        binding.run {
            sendButton.isInvisible = true
            sendProgress.isVisible = true
        }
    }

    protected fun onMessageSendSuccess() {
        binding.run {
            sendButton.isInvisible = false
            sendProgress.isVisible = false
            messageInput.text.clear()
        }
    }

    protected fun onMessageSendFailed() {
        binding.run {
            sendButton.isInvisible = false
            sendProgress.isVisible = false
            Toast.makeText(requireContext(), "Failed to send message", Toast.LENGTH_LONG).show()
        }
    }

    protected open fun getMessage() =
        Message().apply { text = binding.messageInput.text.toString() }

    protected fun handleError(error: ChatError) {
        Timber.e(error)
        Toast.makeText(requireContext(), "Error occurred!!!", Toast.LENGTH_SHORT).show()
    }

    protected fun scrollToLast() {
        binding.messageRecycler.post {
            binding.messageRecycler.smoothScrollToPosition(
                binding.messageRecycler.adapter?.itemCount ?: 0
            )
        }
    }

    companion object {
        const val CHANNEL_TYPE = "livestream"
        const val CHANNEL_ID = "channel_id"
        const val MESSAGE_LIMIT = 100
    }
}