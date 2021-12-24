package com.digitalsignage.androidplayer.remote.interceptor

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.digitalsignage.androidplayer.utils.AppLog
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class NetworkInterceptor(private val context: Context) : Interceptor {

    val networkMessage = "No network available, please check your WiFi or Data connection"
    override fun intercept(chain: Interceptor.Chain): Response {
        if (isNetworkAvailable(context))
        {
            val request = chain.request()
            val response = chain.proceed(request)
            return response
        }else {
            backgroundThreadShortToast(context,networkMessage)
            throw NoConnectivityException()
        }
    /* if (isNetworkAvailable(context)){
            try {
                val response = chain.proceed(request)

                when(response.code)
                {
                    401,500 -> {

                    }
                }
                //return response
            }catch (e: Exception)
            {
                AppLog.d("NetworkInterceptor","Error Occurred NI")
            }
        }else
        {
            throw NoConnectivityException()
        }
        return null*/
    }


    //custom exceptions
    class NoConnectivityException : IOException() {
        override val message: String
            get() =
                "No network available, please check your WiFi or Data connection"
    }

    class NoInternetException() : IOException() {
        override val message: String
            get() =
                "No internet available, please check your connected WIFi or Data"
    }
}

fun backgroundThreadShortToast(context: Context, msg: String)
{
    if(context != null && msg != null)
    {
       Handler(Looper.getMainLooper()).post(Runnable {
           Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
       });
    }
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (connectivityManager != null)
    {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            val nwInfo = connectivityManager.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }
    return false
}
