package com.mctech.library.keyboard.visibilitymonitor

import android.content.res.Configuration
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * @author MAYCON CARDOSO on 2019-12-16.
 */
class KeyboardVisibilityMonitor(
    lifecycleOwner: LifecycleOwner,
    private val activity: AppCompatActivity,
    private val keyboardObservableSettings: KeyboardObservableSettings = KeyboardObservableSettings(),
    private val onChangeCallback: (change: KeyboardChange) -> Unit
) : PopupWindow(activity), LifecycleObserver {

    private val activityMainContainer: View
    private val popupView: View?

    private var countTimesNotified = 0
    private var lastChange: KeyboardChange? = null

    private val onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        handleOnGlobalLayout()
    }

    init {
        // Create popup view.
        this.popupView = FrameLayout(activity).apply {

            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )

        }
        contentView = popupView

        // Set soft input mode on the popup instance.
        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        inputMethodMode = INPUT_METHOD_NEEDED

        // Set the size of the popup.
        width = 0
        height = WindowManager.LayoutParams.MATCH_PARENT

        // Resolve activity main container.
        activityMainContainer = activity.findViewById(android.R.id.content)

        // Observe lifecycle to avoid memory leak.
        lifecycleOwner.lifecycle.addObserver(this)

        // Observe changes
        lifecycleOwner.lifecycleScope.launchWhenResumed {
            while (!isShowing && isActive) {
                showPopup()
                delay(100)
            }
        }
    }

    /**
     * Close the KeyboardVisibilityMonitor
     * this provider will not be used anymore.
     */
    @OnLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
    fun close() {
        removeLayoutChanges()
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
        popupView?.getWindowVisibleDisplayFrame(rect)

        // REMIND, you may like to change this using the fullscreen size of the phone
        // and also using the status bar and navigation bar heights of the phone to calculate
        // the keyboard height. But this worked fine on a Nexus.
        val orientation = activity.resources.configuration.orientation
        val keyboardHeight = screenSize.y - rect.bottom

        when {
            keyboardHeight == 0 -> notifyKeyboardHeightChanged(0)
            orientation == Configuration.ORIENTATION_PORTRAIT -> notifyKeyboardHeightChanged(
                keyboardHeight
            )
            else -> notifyKeyboardHeightChanged(keyboardHeight)
        }
    }

    private fun notifyKeyboardHeightChanged(height: Int) = synchronized(keyboardObservableSettings) {

            // This is the first notification and the user has said he wouldn't like receive the first notification.
            if (++countTimesNotified <= 2 && !keyboardObservableSettings.notifyWhenScreenHasOpenedAtFirstTime) {
                return@synchronized
            }

            // This is the new state of keyboard.
            val newState = KeyboardChange(
                isOpened = height > 0,
                currentKeyboardHeight = height
            )

            // The state is the same and the user has said the would like to receive only new changes.
            lastChange?.takeIf { it.isOpened == newState.isOpened }
                ?.takeIf { keyboardObservableSettings.notifyOnlyWhenStateChange }?.let {
                return@synchronized
            }

            // Send new notification to the client.
            onChangeCallback.invoke(
                newState.apply {
                    lastChange = this
                }
            )
        }

    private fun showPopup() = synchronized(isShowing) {
        if (isShowing)
            return

        if (activityMainContainer.windowToken == null)
            return

        setBackgroundDrawable(ColorDrawable(0))
        showAtLocation(activityMainContainer, Gravity.NO_GRAVITY, 0, 0)

        registerKeyboardChanges()
    }

    private fun registerKeyboardChanges() {
        // Remove existent callback
        removeLayoutChanges()

        // Observe view changes to notify keyboard.
        popupView?.viewTreeObserver?.addOnGlobalLayoutListener(onGlobalLayoutListener)
    }

    private fun removeLayoutChanges() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            popupView?.viewTreeObserver?.removeOnGlobalLayoutListener(onGlobalLayoutListener)
        } else {
            popupView?.viewTreeObserver?.removeGlobalOnLayoutListener(onGlobalLayoutListener)

        }
    }
}