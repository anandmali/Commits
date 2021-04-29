package com.anandmali.commits.util

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.anandmali.commits.R
import com.google.android.material.snackbar.Snackbar

inline fun Activity.showSnackBar(
    message: String,
    textColor: Int? = null,
    length: Int = Snackbar.LENGTH_LONG,
    f: Snackbar.() -> Unit = {}
) {
    findViewById<View>(android.R.id.content).snack(message, textColor, length, f)
}

inline fun Fragment.showSnackBar(
    message: String,
    textColor: Int? = null,
    length: Int = Snackbar.LENGTH_LONG,
    f: Snackbar.() -> Unit = {}
) {
    activity?.showSnackBar(message, textColor, length, f)
}


inline fun View.snack(
    message: String,
    textColor: Int? = null,
    length: Int = Snackbar.LENGTH_LONG,
    f: Snackbar.() -> Unit
) {
    val snack = Snackbar.make(this, message, length)
    val textView = snack.view.findViewById<TextView>(R.id.snackbar_text)
    textView.maxLines = 5
    textColor?.let {
        textView.setTextColor(textColor)
    }
    snack.f()
    snack.show()
}
