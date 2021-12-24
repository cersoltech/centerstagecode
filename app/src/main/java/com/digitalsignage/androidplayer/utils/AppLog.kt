package com.digitalsignage.androidplayer.utils

import android.util.Log

object AppLog {
 private const val TAG = "AndroidPlayer"

    fun d(tag: String = TAG,message: String)
    {
        Log.d(tag, message)
    }
    fun e(tag: String = TAG,message: String)
    {
        Log.e(tag, message)
    }

    fun i(tag: String = TAG,message: String)
    {
        Log.i(tag, message)
    }
    fun v(tag: String = TAG,message: String)
    {
        Log.v(tag, message)
    }
}