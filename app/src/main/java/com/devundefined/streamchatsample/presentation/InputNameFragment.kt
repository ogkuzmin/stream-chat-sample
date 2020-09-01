package com.devundefined.streamchatsample.presentation

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.devundefined.streamchatsample.R
import com.devundefined.streamchatsample.databinding.FragmentInputNameBinding
import com.devundefined.streamchatsample.domain.UserExtensions
import com.devundefined.streamchatsample.domain.UserRepo
import com.devundefined.streamchatsample.presentation.simplechat.SimpleChatFragment
import org.koin.android.ext.android.inject

class InputNameFragment : Fragment(R.layout.fragment_input_name) {

    private lateinit var binding: FragmentInputNameBinding

    private val userRepo: UserRepo by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInputNameBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.nameInput.requestFocus()
        binding.nameInput.addTextChangedListener { text: Editable? ->
            binding.saveButton.isEnabled = text?.length ?: 0 >= MIN_NAME_LENGTH
        }
        binding.saveButton.setOnClickListener {
            userRepo.save(UserExtensions.createUser(binding.nameInput.text.toString()))
            showChatFragment()
        }
    }

    private fun showChatFragment() {
        (activity as? MainActivity)?.showFragment(SimpleChatFragment())
    }

    companion object {
        private const val MIN_NAME_LENGTH = 4
    }
}