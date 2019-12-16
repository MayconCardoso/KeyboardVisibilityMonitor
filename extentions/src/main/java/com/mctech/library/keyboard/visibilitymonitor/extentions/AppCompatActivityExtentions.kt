package com.mctech.library.keyboard.visibilitymonitor.extentions

import androidx.appcompat.app.AppCompatActivity
import com.mctech.library.keyboard.visibilitymonitor.KeyboardChange
import com.mctech.library.keyboard.visibilitymonitor.KeyboardObservableSettings
import com.mctech.library.keyboard.visibilitymonitor.KeyboardVisibilityMonitor

fun AppCompatActivity.observeKeyboardChanges(
    settings: KeyboardObservableSettings = KeyboardObservableSettings(),
    onKeyBoardChangeBlock: (change: KeyboardChange) -> Unit
) {
    KeyboardVisibilityMonitor(
        lifecycleOwner = this.findLifeCycleOwner(),
        activity = this,
        keyboardObservableSettings = settings,
        onChangeCallback = {
            onKeyBoardChangeBlock.invoke(it)
        }
    )
}