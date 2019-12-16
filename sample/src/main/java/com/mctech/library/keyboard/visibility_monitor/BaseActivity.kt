package com.mctech.library.keyboard.visibility_monitor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.mctech.library.keyboard.visibilitymonitor.KeyboardObservableSettings
import com.mctech.library.keyboard.visibilitymonitor.KeyboardVisibilityMonitor

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_edittext)
    }

    override fun onStart() {
        super.onStart()

        val keyboardObservableSettings = KeyboardObservableSettings(
            notifyWhenScreenHasOpenedAtFirstTime = true,
            notifyOnlyWhenStateChange = false
        )

        KeyboardVisibilityMonitor(this, this, keyboardObservableSettings){
            findViewById<TextView>(R.id.tvStatus).text = "The keyboard is " + if(it.isOpened) "Opened" else "Closed"
            findViewById<TextView>(R.id.tvSize).text = "Keyboard size: ${it.currentKeyboardHeight}px"
        }
    }
}
