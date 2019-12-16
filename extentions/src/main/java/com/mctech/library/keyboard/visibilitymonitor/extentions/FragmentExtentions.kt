package com.mctech.library.keyboard.visibilitymonitor.extentions

import androidx.fragment.app.Fragment
import com.mctech.library.keyboard.visibilitymonitor.KeyboardChange
import com.mctech.library.keyboard.visibilitymonitor.KeyboardObservableSettings
import com.mctech.library.keyboard.visibilitymonitor.KeyboardVisibilityMonitor

fun Fragment.observeKeyboardChanges(
    settings: KeyboardObservableSettings = KeyboardObservableSettings(),
    onKeyBoardChangeBlock: (change: KeyboardChange) -> Unit
) {
    val context = this.context ?: throw RuntimeException("The provided context cannot be null.")

    KeyboardVisibilityMonitor(
        lifecycleOwner = context.findLifeCycleOwner(),
        activity = context.findActivity(),
        keyboardObservableSettings = settings,
        onChangeCallback = onKeyBoardChangeBlock
    )
}