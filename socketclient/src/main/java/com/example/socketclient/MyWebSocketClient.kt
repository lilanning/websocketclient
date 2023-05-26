package com.example.socketclient

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI


class MyWebSocketClient(val context:Context,serverUri: String?) : WebSocketClient(URI(serverUri)) {


    var start_time_client = System.currentTimeMillis()
    val time1 = ArrayList<Long>()
    val time2 = ArrayList<Long>()
    override fun onOpen(serverHandshake: ServerHandshake) {
        println("WebSocket opened: " + getURI())
    }

    override fun onClose(i: Int, s: String, b: Boolean) {
        println("WebSocket closed: $s")

    }

    fun setStartTime(time:Long){
        start_time_client = time
    }

    override fun onMessage(s: String) {
        println("WebSocket message received: $s")
        if ("what？".equals(s)){
            MessageManager.getListener()?.handleMessage()
            return
        }
        //负控制器客户端必有
        MessageManager.messageListener?.seekTo(s)

    }

    override fun onError(e: Exception) {
        println("WebSocket error: " + e.message)
    }
}