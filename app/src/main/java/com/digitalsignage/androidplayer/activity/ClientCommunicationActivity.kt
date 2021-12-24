package com.digitalsignage.androidplayer.activity

import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.digitalsignage.androidplayer.R
import java.io.IOException
import java.net.*
import java.net.InetAddress.getByName

class SoftOptions {
    //var remoteHost: String = "192.168.1.255"
    //var remotePort: Int = 6454

    constructor()
    init{}
}


// Global
val Settings = SoftOptions()

open class ClientCommunicationActivity : AppCompatActivity() {
    var textViewReceive: TextView? = null
    private val runningThread = false
    //local
    //var remoteHost: String = "127.0.0.1"
    //var remotePort: Int = 5000
    var remotePort: Int = 5555

    //server
    //var remoteHost: String = "172.16.1.12"
    //var remoteHost: String = "10.0.2.2"
    //var remoteHost: String = "192.168.12.128"
    var remoteHost: String = "192.168.0.100"
    //emulator ip
    //var remoteHost: String = "192.168.232.2"
    //var remoteHost: String = "127.0.0.1"
    //var remotePort: Int = 1050

    private var threadWithRunnable: Runnable? = null
    private var threadWith: Thread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_communication)

        textViewReceive = findViewById(R.id.txt_receive)

        println("Create Runnable example.")

        /* threadWithRunnable = Thread(udp_DataArrival())
         threadWithRunnable.start()*/
        threadWithRunnable = udp_DataArrival()
        threadWith = Thread(threadWithRunnable)
        threadWith!!.start()
        sendUDP("Hi")
        //val isOpen = isPortOpen(remoteHost,remotePort,2000)
        //Log.d("pppp", "onCreate: isopen: $isOpen ")
    }

    fun sendUDP(messageStr: String) {
        // Hack Prevent crash (sending should be done using an async task)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        try {
            //Open a port to send the package
            val socket = DatagramSocket()
            socket.broadcast = true
            socket.reuseAddress = true
            val sendData = messageStr.toByteArray()
            val sendPacket = DatagramPacket(sendData, sendData.size, InetAddress.getByName("192.168.0.103"), 63389)
            socket.send(sendPacket)
            println("fun sendBroadcast: packet sent to: " + InetAddress.getByName(remoteHost) + ":" + remotePort)
            val opTxt = "Server details - ${InetAddress.getByName(remoteHost)} : ${remotePort}"
            val textViewSend = findViewById<TextView>(R.id.txt_send)
            //txt_send.text = opTxt
            runOnUiThread { textViewSend.text = opTxt }
        } catch (e: IOException) {
            //Log.e(FragmentActivity.TAG, "IOException: " + e.message)
        }
    }

    open fun receiveUDP() {
        val buffer = ByteArray(2048)
        var socket: DatagramSocket? = null
        try {
            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            //socket = DatagramSocket(remotePort, getByName("localhost"))
            //socket = DatagramSocket(remotePort, getByName(" 0.0.0.0"))
            socket = DatagramSocket(remotePort, getByName(remoteHost))
            socket.broadcast = true
            socket.reuseAddress = true
            //socket.bind(InetSocketAddress(remotePort))
            if (socket.isBound()){
                runOnUiThread {
                    Toast.makeText(this, "Connected to Server...", Toast.LENGTH_SHORT).show()
                }

            }
            val packet = DatagramPacket(buffer, buffer.size)
            socket.receive(packet)
            println("open fun receiveUDP packet received = " + packet.data)
            val textReceive = String(buffer, 0, packet.length)
            println("Received data $textReceive")
            //txt_receive.text = text
            runOnUiThread {
                textViewReceive?.text = textReceive
            }
        } catch (e: Exception) {
            println("open fun receiveUDP catch exception.$e")
            e.printStackTrace()
        } finally {
            socket?.close()
        }
    }

    inner class udp_DataArrival: Runnable{
        override fun run() {
            println("${Thread.currentThread()} Runnable Thread Started.")
            /*while (true) {
                receiveUDP()
            }*/
            /*try {
                while(!Thread.currentThread().isInterrupted()) {
                    // ...
                }
            } catch (consumed: InterruptedException){
                *//* Allow thread to exit *//*
            }*/


            val buffer = ByteArray(2048)
            var socket: DatagramSocket? = null
            try {
                //Keep a socket open to listen to all the UDP trafic that is destined for this port
                //socket = DatagramSocket(remotePort, getByName("localhost"))
                //socket = DatagramSocket(remotePort, getByName(" 0.0.0.0"))
                    if(socket == null)
                    {
                        socket = DatagramSocket(remotePort, getByName(remoteHost))
                        socket.broadcast = true
                        socket.reuseAddress = true
                        //socket.bind( InetSocketAddress(8000))
                    }
                /*socket = DatagramSocket(remotePort, getByName(remoteHost))
                socket.broadcast = true
                socket.reuseAddress = true*/
                //socket.bind(InetSocketAddress(remotePort))
                if (socket.isBound()){
                    println("socket is bound")
                    /*runOnUiThread {
                        Toast.makeText(this, "Connected to Server...", Toast.LENGTH_SHORT).show()
                    }*/

                }
                val packet = DatagramPacket(buffer, buffer.size)
                while (true)
                {
                    socket.receive(packet)
                    println("open fun receiveUDP packet received = " + packet.data)
                    val textReceive = String(buffer, 0, packet.length)
                    println("Received data $textReceive")
                    //txt_receive.text = text
                    runOnUiThread {
                        textViewReceive?.text = textReceive
                    }
                    packet.length = buffer.size
                }
            } catch (e: Exception) {
                println("open fun receiveUDP catch exception.$e")
                e.printStackTrace()
            } finally {
                socket?.close()
            }

        }

    }

    override fun onStop() {
        super.onStop()
        Log.d("socket", "onStop: called")
        if (threadWithRunnable != null)
        {
            threadWithRunnable = null

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("socket", "onDestroy: called ")

    }

    fun isPortOpen(ip: String?, port: Int, timeout: Int): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress(ip, port), timeout)
            socket.close()
            true
        } catch (ce: ConnectException) {
            ce.printStackTrace()
            false
        } catch (ex: Exception) {
            ex.printStackTrace()
            false
        }
    }

}