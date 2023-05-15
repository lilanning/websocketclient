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
    override fun onOpen(serverHandshake: ServerHandshake) {
        println("WebSocket opened: " + getURI())
    }

    override fun onClose(i: Int, s: String, b: Boolean) {
        println("WebSocket closed: $s")

    }

    override fun onMessage(s: String) {
        println("WebSocket message received: $s")

        val handler =object : Handler(Looper.getMainLooper()){
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
            }
        }
//        handler.post{
//            Toast.makeText(context,"${s.toString()}",Toast.LENGTH_SHORT).show()
//        }
        if ("what？".equals(s)){
            MessageManager.getListener()?.handleMessage()
        }

    }

    override fun onError(e: Exception) {
        println("WebSocket error: " + e.message)
    }
}