package com.devundefined.streamchatsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    external fun getApiKey(): String
    external fun getSecretKey(): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        val textView = findViewById<TextView>(R.id.secrets)
        textView.text = "Secrets:\nApi key is \"${getApiKey()}\"\nSecret key is \"${getSecretKey()}\""
    }

    companion object {
        init {
            System.loadLibrary("keys")
        }
    }
}