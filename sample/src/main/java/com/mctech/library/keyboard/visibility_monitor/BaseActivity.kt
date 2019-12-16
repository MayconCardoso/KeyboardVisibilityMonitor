package com.mctech.library.keyboard.visibility_monitor

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.mctech.library.keyboard.visibilitymonitor.KeyboardObservableSettings
import com.mctech.library.keyboard.visibilitymonitor.extentions.observeKeyboardChanges

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_edittext)

        val settings = KeyboardObservableSettings(
            notifyWhenScreenHasOpenedAtFirstTime = true,
            notifyOnlyWhenStateChange = false
        )

        val textKeyboardState = findViewById<TextView>(R.id.tvStatus)
        val textKeyboardSize  = findViewById<TextView>(R.id.tvSize)

        observeKeyboardChanges(settings) {
            textKeyboardState.text = "The keyboard is " + if(it.isOpened) "opened" else "closed"
            textKeyboardSize.text  = "Keyboard size: ${it.currentKeyboardHeight}px"
        }
    }
}
