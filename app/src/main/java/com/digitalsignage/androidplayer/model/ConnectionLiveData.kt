package com.digitalsignage.androidplayer.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.digitalsignage.androidplayer.App
import com.digitalsignage.androidplayer.remote.interceptor.isNetworkAvailable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi


@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class ConnectionLiveData(val context: Context): LiveData<Boolean>() {

    private var netConnectionReceiver: NetworkReceiver? = null

    val MobileData = 2
    val WifiData = 1

    override fun onActive() {
        super.onActive()

    }

    override fun onInactive() {
        super.onInactive()

    }

    class NetworkReceiver : BroadcastReceiver() {

        interface ConnectivityReceiverListener {
            fun onNetworkConnectionChanged(isConnected: Boolean)
        }

        companion object {
            var connectivityReceiverListener: ConnectivityReceiverListener? = null
        }

        var isNetConnected = MutableLiveData<Boolean>()

        override fun onReceive(context: Context?, intent: Intent?) {

            if (connectivityReceiverListener != null) {
                connectivityReceiverListener!!.onNetworkConnectionChanged(isNetworkAvailable(context!!))
            }
        }

    }
}