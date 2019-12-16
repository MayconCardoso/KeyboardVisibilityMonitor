package com.mctech.library.keyboard.visibility_monitor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
    }

    fun onClickAdjustNothing(view: View) {
        startActivity(Intent(this, AdjustNothingActivity::class.java))
    }

    fun onClickAdjustPan(view: View) {
        startActivity(Intent(this, AdjustPanActivity::class.java))
    }

    fun onClickAdjustResize(view: View) {
        startActivity(Intent(this, AdjustResizeActivity::class.java))
    }

    fun onClickAdjustUnspecified(view: View) {
        startActivity(Intent(this, AdjustUnspecifiedActivity::class.java))
    }
}
