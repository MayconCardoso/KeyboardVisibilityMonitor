package com.mctech.library.keyboard.visibilitymonitor.extentions

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.mctech.library.keyboard.visibilitymonitor.KeyboardChange
import com.mctech.library.keyboard.visibilitymonitor.KeyboardObservableSettings
import com.mctech.library.keyboard.visibilitymonitor.KeyboardVisibilityMonitor

fun Context.findLifeCycleOwner() : LifecycleOwner {
    return when(this){
        is LifecycleOwner -> this
        is android.view.ContextThemeWrapper -> this.baseContext as LifecycleOwner
        is androidx.appcompat.view.ContextThemeWrapper -> this.baseContext as LifecycleOwner
        else -> throw IllegalArgumentException("The provided context is not a LifecycleOwner")
    }
}

fun Context.findActivity() : AppCompatActivity {
    return when(this){
        is AppCompatActivity -> this
        is android.view.ContextThemeWrapper -> this.baseContext as AppCompatActivity
        is androidx.appcompat.view.ContextThemeWrapper -> this.baseContext as AppCompatActivity
        else -> throw IllegalArgumentException("The provided context is not a Activity")
    }
}

fun Context.observeKeyboardChanges(
    settings: KeyboardObservableSettings = KeyboardObservableSettings(),
    onKeyBoardChangeBlock: (change: KeyboardChange) -> Unit
) {
    KeyboardVisibilityMonitor(
        lifecycleOwner = this.findLifeCycleOwner(),
        activity = this.findActivity(),
        keyboardObservableSettings = settings,
        onChangeCallback = onKeyBoardChangeBlock
    )
}

fun Context.observeKeyboardWhenOpened(
    settings: KeyboardObservableSettings = KeyboardObservableSettings(),
    onKeyBoardChangeBlock: (change: KeyboardChange) -> Unit
) = observeKeyboardChanges(settings) {
    if(it.isOpened){
        onKeyBoardChangeBlock.invoke(it)
    }
}

fun Context.observeKeyboardWhenClosed(
    settings: KeyboardObservableSettings = KeyboardObservableSettings(),
    onKeyBoardChangeBlock: (change: KeyboardChange) -> Unit
) = observeKeyboardChanges(settings) {
    if(!it.isOpened){
        onKeyBoardChangeBlock.invoke(it)
    }
}