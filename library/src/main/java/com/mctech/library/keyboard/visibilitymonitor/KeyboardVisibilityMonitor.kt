package com.mctech.library.keyboard.visibilitymonitor

import android.content.res.Configuration
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

/**
 * @author MAYCON CARDOSO on 2019-12-16.
 */
class KeyboardVisibilityMonitor(
    lifecycleOwner: LifecycleOwner,
    private val activity : FragmentActivity,
    private val onChangeCallback : (change : KeyboardChange) -> Unit
) : PopupWindow(activity), LifecycleObserver {

    private val activityMainContainer: View

    init {

        // Create popup view.
        this.contentView = FrameLayout(activity).apply {

            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )

        }

        // Set soft input mode on the popup instance.
        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        inputMethodMode = INPUT_METHOD_NEEDED

        // Set the size of the popup.
        width = 0
        height = WindowManager.LayoutParams.MATCH_PARENT

        // Resolve activity main container.
        activityMainContainer = activity.findViewById(android.R.id.content)

        // Observe view tree to notify when the keyboard changes.
        activityMainContainer.viewTreeObserver?.addOnGlobalLayoutListener {
            if(isShowing){
                handleOnGlobalLayout()
            }
            else{
                start()
            }
        }

        // Observe lifecycle to avoid memory leak.
        lifecycleOwner.lifecycle.addObserver(this)
    }

    /**
     * Start the KeyboardVisibilityMonitor.
     * This must be called after the onResume of the Activity.
     * PopupWindows are not allowed to be registered before the onResume has finished of the Activity.
     */
    @OnLifecycleEvent(value = Lifecycle.Event.ON_RESUME)
    fun start() {
        if (activityMainContainer.windowToken != null) {
            setBackgroundDrawable(ColorDrawable(0))
            showAtLocation(activityMainContainer, Gravity.NO_GRAVITY, 0, 0)
        }
    }

    /**
     * Close the KeyboardVisibilityMonitor
     * this provider will not be used anymore.
     */
    @OnLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
    fun close() {
        dismiss()
    }

    /**
     * Popup window itself is as big as the window of the Activity.
     * The keyboard can then be calculated by extracting the popup view bottom
     * from the activity window height.
     */
    private fun handleOnGlobalLayout() {

        val screenSize = Point()
        activity.windowManager.defaultDisplay.getSize(screenSize)

        val rect = Rect()
        activityMainContainer.getWindowVisibleDisplayFrame(rect)

        // REMIND, you may like to change this using the fullscreen size of the phone
        // and also using the status bar and navigation bar heights of the phone to calculate
        // the keyboard height. But this worked fine on a Nexus.
        val orientation = activity.resources.configuration.orientation
        val keyboardHeight = screenSize.y - rect.bottom

        when {
            keyboardHeight == 0 -> notifyKeyboardHeightChanged(0)
            orientation == Configuration.ORIENTATION_PORTRAIT -> notifyKeyboardHeightChanged(keyboardHeight)
            else -> notifyKeyboardHeightChanged(keyboardHeight)
        }
    }

    private fun notifyKeyboardHeightChanged(height: Int) {
        onChangeCallback.invoke(KeyboardChange(
            isOpened = height > 0,
            currentKeyboardHeight = height
        ))
    }
}