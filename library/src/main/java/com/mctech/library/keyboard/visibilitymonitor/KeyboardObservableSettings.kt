package com.mctech.library.keyboard.visibilitymonitor

data class KeyboardObservableSettings(
    // When true the callback will be called when the screen has opened at the first time.
    val notifyWhenScreenHasOpenedAtFirstTime : Boolean = true,

    // When true the callback will be called only when the state 'Opened/Closed' has changed.
    // Sometimes, when you have multiple EditText on the screen with different InputTypes (Number, Text, etc)
    // The height of the keyboard may change but it doesn't mean that the state has changed.
    // You could set this variable to false when you want to animate your screen according the keyboard location on the screen.
    val notifyOnlyWhenStateChange : Boolean = true
)