package com.digitalsignage.androidplayer.fileutils

import android.content.Context
import android.content.Intent
import android.util.Log
import java.net.DatagramPacket
import java.net.DatagramSocket


object UdpReceiver {
    private const val serverActive = true
    private const val MESSAGE_RECEIVED = "MESSAGE_RECEIVED"
    private const val MESSAGE_STRING = "MESSAGE_STRING"

     fun listenUdpResult(context: Context){
        val lMsg = ByteArray(4096)
        val dp = DatagramPacket(lMsg, lMsg.size)
        var ds: DatagramSocket? = null

        try {
            ds = DatagramSocket(1050)
            Log.d("udp", "listenUdpResult: ${ds?.isConnected} \n " +
                    "${ds?.isBound}")
            //ds = DatagramSocket(1050,InetAddress.getByName("192.168.1.113"))
            //ds = new DatagramSocket(MainActivityKt.SERVER_PORT);
            //ds.setSoTimeout(10000);
            while (serverActive) {
                ds.receive(dp)
                val i = Intent()
                i.action = MESSAGE_RECEIVED
                i.putExtra(MESSAGE_STRING, String(lMsg, 0, dp.length))
                context.applicationContext.sendBroadcast(i)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("udp error", "listenUdpResult: ${e.message}")
            //serverActive = false
        } finally {
            if(ds != null) {
                ds.close()
            }
        }
    }
}