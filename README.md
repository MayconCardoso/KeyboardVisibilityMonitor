Keyboard Visibility Monitor
=

We all know how hard it is to manage keyboard changes on Android. Sometimes we need to do something when the keyboard is open or closed. How do we do it? There is no easy way.

This library is designed to make it easier to monitor keyboard changes. So, in order to keep things compatible with Java and Kotlin the library has been split into two: core and ktx.

## Download
```groovy
// IMPORTANT
// These libraries are being uploading to Jcenter. If it is not working try again in a few hours.
implementation com.mctech.library.keyboard:visibilitymonitor
implementation com.mctech.library.keyboard:visibilitymonitor-ktx
``` 

## Show me the code :)
All you have to do is create an instance of [KeyboardVisibilityMonitor](https://github.com/MayconCardoso/KeyboardVisibilityMonitor/blob/master/library/src/main/java/com/mctech/library/keyboard/visibilitymonitor/KeyboardVisibilityMonitor.kt) which is a [Lifecycle-Aware Component](https://developer.android.com/topic/libraries/architecture/lifecycle) inside an activity or fragment.

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    ...
    KeyboardVisibilityMonitor( lifecycleOwner = this, activity = this){ change ->
        // Your code here
    }
}
```

It will deliver you an instance of [KeyboardChange](https://github.com/MayconCardoso/KeyboardVisibilityMonitor/blob/master/library/src/main/java/com/mctech/library/keyboard/visibilitymonitor/KeyboardChange.kt) every time the keyboard state changes.

```kotlin
data class KeyboardChange(
    val isOpened: Boolean,
    val currentKeyboardHeight: Int
)
```

## Custom Settings
You can change the keyboard notifier policy by creating an instance of [KeyboardObservableSettings](https://github.com/MayconCardoso/KeyboardVisibilityMonitor/blob/master/library/src/main/java/com/mctech/library/keyboard/visibilitymonitor/KeyboardObservableSettings.kt)

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    ...
    val settings = KeyboardObservableSettings(
        notifyWhenScreenHasOpenedAtFirstTime = false,
        notifyOnlyWhenStateChange = true
    )

    KeyboardVisibilityMonitor( 
        lifecycleOwner = this, 
        activity = this, 
        keyboardObservableSettings = settings
    ){ change ->
        // Your code here
    }
}
```

## Kotlin extentions
Of course, the code above could be better and without that all boilerplate. Just make sure you have implemented the ```com.mctech.library.keyboard:visibilitymonitor-ktx``` on your ```build.gradle``` dependencies block. For more information about all available extentions [click here](https://github.com/MayconCardoso/KeyboardVisibilityMonitor/tree/master/extentions/src/main/java/com/mctech/library/keyboard/visibilitymonitor/extentions)

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    ...
    observeKeyboardChanges {
        // Your code here
    }
    
    // Or you can call the same method with the settings mentioned above.
    val settings = KeyboardObservableSettings(
        notifyWhenScreenHasOpenedAtFirstTime = false,
        notifyOnlyWhenStateChange = true
    )

    observeKeyboardChanges(settings) { 
        // Your code here
    }
}
```
