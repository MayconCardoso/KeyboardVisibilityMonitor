package com.mctech.library.keyboard.visibilitymonitor.extentions

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.EditText

fun Context.openKeyboardOnView(view: EditText) {
    val inputMethodManager = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
    inputMethodManager?.showSoftInput(view, SHOW_IMPLICIT)
}

fun Context.closeKeyboard(editText: EditText? = null) {
    val inputMethodManager = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
    inputMethodManager?.hideSoftInputFromWindow(editText?.windowToken, 0)
}

fun EditText.openKeyboard() {
    val inputMethodManager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
    inputMethodManager?.showSoftInput(this, SHOW_IMPLICIT)
}

fun EditText.closeKeyboard() {
    val inputMethodManager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
    inputMethodManager?.hideSoftInputFromWindow(this.windowToken, 0)
}