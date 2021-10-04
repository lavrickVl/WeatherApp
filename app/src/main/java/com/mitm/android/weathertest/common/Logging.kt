package com.mitm.android.weathertest.common

import android.util.Log

object Logging {

    operator fun invoke(msg: String, TAG: String = "myLog") {
        Log.d(TAG, "$msg: ")
    }
}