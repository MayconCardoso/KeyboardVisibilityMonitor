package com.mctech.library.keyboard.visibilitymonitor

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.DisplayMetrics
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
    private var shouldConsiderActionBarSize = false

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
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

        val rect = Rect()
        popupView?.getWindowVisibleDisplayFrame(rect)

        val keyboardHeight =
            if(rect.bottom > displayMetrics.heightPixels || rect.bottom == displayMetrics.heightPixels)
                0
            else
                displayMetrics.heightPixels - rect.bottom + getStatusBarHeight(activity)

        if(keyboardHeight == 0){
            shouldConsiderActionBarSize = rect.bottom > displayMetrics.heightPixels
        }

        when (keyboardHeight) {
            0 -> notifyKeyboardHeightChanged(0)
            else -> notifyKeyboardHeightChanged(keyboardHeight)
        }
    }

    private fun getStatusBarHeight(context: Context): Int {
        if(!shouldConsiderActionBarSize) return 0

        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    private fun notifyKeyboardHeightChanged(height: Int) =
        synchronized(keyboardObservableSettings) {
            // This is the first notification and the user has said he wouldn't like receive the first notification.
            if (++countTimesNotified <= 2 && !keyboardObservableSettings.notifyWhenScreenHasOpenedAtFirstTime) {
                return@synchronized
            }

            // This is the new state of keyboard.
            val newState = KeyboardChange(
                isOpened = height > 0,
                currentKeyboardHeight = if(height > 0) height else 0
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