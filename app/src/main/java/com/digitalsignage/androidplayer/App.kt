package com.digitalsignage.androidplayer

import androidx.multidex.MultiDexApplication
import com.digitalsignage.androidplayer.utils.PreferanceRepository

class App: MultiDexApplication() {

    companion object{
        private lateinit var instance: App

        @Synchronized
        fun getInstance(): App = instance
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        PreferanceRepository.init(this)
    }

    /*object Variables {
        var isNetworkConnected: Boolean by Delegates.observable(false) {
                property, oldValue, newValue ->
            AppLog.i("APP","Network connectivity has $newValue")
        }
    }


    private var showTransition : Boolean = false

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startNetworkCallback() {
        val cm: ConnectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val builder : NetworkRequest.Builder = NetworkRequest.Builder()

        cm.registerNetworkCallback(
            builder.build(),
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    AppLog.i("APP","Network available")
                    if (showTransition) {
                        Toast.makeText(
                            applicationContext, getString(R.string.NetworkAvailable),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    showTransition = false
                    Variables.isNetworkConnected = true
                    super.onAvailable(network)
                }
                override fun onLost(network: Network) {
                    AppLog.i("APP", "Network lost")
                    Toast.makeText(applicationContext,getString(R.string.NetworkUnavailable),
                        Toast.LENGTH_LONG).show()
                    Variables.isNetworkConnected = false
                    showTransition = true
                    super.onLost(network)
                }
            }
        )

    }*/
}