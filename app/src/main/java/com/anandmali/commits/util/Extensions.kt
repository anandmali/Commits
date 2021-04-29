package com.anandmali.commits.util

import android.content.res.Resources

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.spToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()


