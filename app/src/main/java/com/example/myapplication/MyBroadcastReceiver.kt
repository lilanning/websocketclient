package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket


class MyBroadcastReceiver :BroadcastReceiver() {
    private val TAG = "MyBroadcastReceiver"
    private val BUFFER_SIZE = 1024

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent!!.action
        if (action != null && action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            // 监听网络变化事件，重新注册UDP广播接收器
            registerBroadcastReceiver(context!!)
        } else {
            // 接收UDP广播消息
            val buffer = ByteArray(BUFFER_SIZE)
            val packet = DatagramPacket(buffer, buffer.size)
            try {
                val socket = DatagramSocket(8888)
                socket.receive(packet)
                val message = String(packet.data, 0, packet.length)
                Log.d(TAG, "Received broadcast message: $message")
                socket.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun registerBroadcastReceiver(context: Context) {
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        filter.addAction("myapp.action.BROADCAST")
        context.registerReceiver(this, filter)
    }

    fun unregisterBroadcastReceiver(context: Context) {
        context.unregisterReceiver(this)
    }

}