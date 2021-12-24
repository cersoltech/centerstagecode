package com.digitalsignage.androidplayer.fileutils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class UdpBroadcastReceiver  : BroadcastReceiver() {

    var udpList = ArrayList<String>()
    private var oldTimeStamp: Number = 0
    //private var searchSubject = PublishSubject.create<String>()

    private var searchSubject: PublishSubject<String>? = PublishSubject.create<String>()
        get() {
            if (field == null) {
                field = PublishSubject.create<String>()
            }
            return field
        }


    companion object{
        private var udpListerner: OnUdpListerner? = null
        private var disposableUdp: Disposable? = null

        fun setUdpListener(listerner: OnUdpListerner?){
            udpListerner = listerner
        }
    }
    init {
        //subscribeUdpResponse()
    }

    //1,2,3,4,5
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e("UdpReceiver", "message received: ${intent?.getStringExtra("MESSAGE_STRING")}")
        //udpList.add(intent?.getStringExtra("MESSAGE_STRING")!!)
        //searchSubject?.onNext(intent?.getStringExtra("MESSAGE_STRING")!!)
        //val diff = System.currentTimeMillis()- oldTimeStamp.toDouble()

        udpListerner?.onUdpReceived(intent?.getStringExtra("MESSAGE_STRING"))
    }

     private fun subscribeUdpResponse(){
      //to give delay of 5sec when receiving udp packets continuously
       disposableUdp = searchSubject?.debounce(5000, TimeUnit.MILLISECONDS)
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    //search(it)
                    oldTimeStamp = System.currentTimeMillis()

                    udpListerner?.onUdpReceived(it)
                    //udpList.removeAt(0)
                }, {

                })
    }

    public fun destroyUdpDisposable(){
        disposableUdp?.dispose()
        searchSubject?.onComplete()
        searchSubject = null
    }
}

interface OnUdpListerner{
    fun onUdpReceived(udpMsg: String?)
}
