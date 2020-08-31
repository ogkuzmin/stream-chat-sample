package com.devundefined.streamchatsample.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.devundefined.streamchatsample.R
import com.devundefined.streamchatsample.databinding.ActivityMainBinding
import com.devundefined.streamchatsample.domain.UserRepo
import com.devundefined.streamchatsample.presentation.chat.ChatFragment
import com.devundefined.streamchatsample.presentation.setup.SetupFragment
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val userRepo: UserRepo by inject()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        if (userRepo.find() == null) {
            showFragment(InputNameFragment())
        } else {
            showFragment(ChatFragment())
        }
        binding.actionButton.setOnClickListener { showPreferencesFragment() }
    }

    private fun showPreferencesFragment() {
        SetupFragment().show(supportFragmentManager, "NO_TAG")
    }

    fun showFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment)
            .commit()

    fun updateToolbarTitle(title: String) {
        binding.toolbar.title = title
    }
}