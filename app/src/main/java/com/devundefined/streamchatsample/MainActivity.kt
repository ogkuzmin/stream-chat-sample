package com.devundefined.streamchatsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        val textView = findViewById<TextView>(R.id.secrets)
        textView.text = "Secrets:\nApi key is \"${getApiKey()}\"\nSecret key is \"${getSecretKey()}\""
    }

    private fun getApiKey() = BuildConfig.API_KEY
    private fun getSecretKey() = BuildConfig.API_SECRET
}