package com.devundefined.streamchatsample.presentation

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.devundefined.streamchatsample.R
import com.devundefined.streamchatsample.domain.UserRepo
import com.devundefined.streamchatsample.infrastructure.KeyValueStorage
import com.devundefined.streamchatsample.infrastructure.Toggles
import com.devundefined.streamchatsample.presentation.simplechat.SimpleChatFragment
import com.devundefined.streamchatsample.presentation.setup.SetupFragment
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val userRepo: UserRepo by inject()
    private val keyValueStorage: KeyValueStorage by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.action_button)?.setOnClickListener { showPreferencesFragment() }
        startFirstScreen()
    }

    private fun startFirstScreen() {
        when {
            userRepo.find() == null ->
                showFragment(InputNameFragment())
            keyValueStorage.get(Toggles.KEY_LIVEDATA_TOGGLE, true) -> showFragment(
                SimpleChatFragment()
            )
            else -> showFragment(SimpleChatFragment())
        }
    }

    private fun showPreferencesFragment() {
        SetupFragment().show(supportFragmentManager, "NO_TAG")
    }

    fun showFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().replace(R.id.container, fragment)
            .commit()

    fun updateToolbarTitle(title: String) {
        findViewById<Toolbar>(R.id.toolbar)?.title = title
    }
}