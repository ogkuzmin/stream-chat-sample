package com.devundefined.streamchatsample.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.devundefined.streamchatsample.R
import com.devundefined.streamchatsample.domain.UserRepo
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val userRepo: UserRepo by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (userRepo.find() == null) {
            supportFragmentManager.beginTransaction().add(R.id.container, InputNameFragment())
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
    }
}