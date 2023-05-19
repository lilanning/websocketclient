package com.example.socketclient

interface MessageListener {
    fun handleMessage()
    fun seekTo(mes :String)
}
object MessageManager{
    var messageListener:MessageListener? = null
    fun setListener(messageListener: MessageListener){
        this.messageListener = messageListener
    }
    fun getListener(): MessageListener? {
        return messageListener
    }
}