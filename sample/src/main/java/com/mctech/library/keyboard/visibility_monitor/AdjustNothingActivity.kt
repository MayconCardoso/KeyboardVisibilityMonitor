package com.mctech.library.keyboard.visibility_monitor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.mctech.library.keyboard.visibilitymonitor.KeyboardVisibilityMonitor

class AdjustNothingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adjust_nothing)
    }

    override fun onStart() {
        super.onStart()
        KeyboardVisibilityMonitor(this, this){
            Toast.makeText(this, if(it.isOpened) "Opened" else "Closed", Toast.LENGTH_LONG).show()
        }
    }
}
