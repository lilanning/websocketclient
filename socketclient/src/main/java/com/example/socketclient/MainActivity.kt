package com.example.socketclient

import android.R.attr.data
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.MediaController
import androidx.activity.ComponentActivity
import androidx.databinding.DataBindingUtil
import com.example.socketclient.databinding.LayoutBinding
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity(),MessageListener {
    private lateinit var layoutBinding: LayoutBinding
    private lateinit var webSocketClient:MyWebSocketClient
    private var startTime = System.currentTimeMillis()
    private val BROADCAST_PORT = 8887
    private val isRuning = true
    private var serverHost = ""
    private val BROADCAST_IP = "255.255.255.255"
    private var start = System.nanoTime()
    private val times = ArrayList<Long>()

    private var isCompleted = false

    private val REQUEST_CODE_SELECT_VIDEO = 100
    val runnable = object : Runnable {
        override fun run() {
            layoutBinding.text.text =
                (System.currentTimeMillis() - startTime).toString() + "ms"
            handler.postDelayed(this, 100)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        layoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.layout, null, false)
        setContentView(layoutBinding.root)
        onBroadcastReceive()
        //val webSocketClient = MyWebSocketClient("ws://192.168.43.2271:8888")
        layoutBinding.start.setOnClickListener {
            layoutBinding.line1.visibility = View.GONE
            Thread{
                try {
                     webSocketClient = MyWebSocketClient(this,"ws://169.254.228.156:8888")
                    Log.d("TAG", "onCreate: "+webSocketClient.readyState)
//                    while (!webSocketClient.readyState.equals(WebSocket.READYSTATE.OPEN)){
//                        println("还没有打开呢")
//                    }
                    webSocketClient.connectBlocking()
                    //webSocketClient.send("World")
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }.start()
        }
        layoutBinding.send.setOnClickListener {
            if (webSocketClient!= null){
                webSocketClient.send("你好，我這邊是客户端")

            }
        }
        layoutBinding.close.setOnClickListener {
            if (webSocketClient != null) {
                try {
                    webSocketClient.closeBlocking()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        val packageName = packageName
        val uri = Uri.parse("android.resource://$packageName/" + R.raw.cc)
        Log.d("MainActivity", "onCreate: ${uri.toString()}")

        layoutBinding.video.setVideoURI(uri)
        val mediaController = MediaController(this)
        layoutBinding.video.setMediaController(mediaController)
        layoutBinding.select.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "video/*"
            startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO)
            layoutBinding.video.stopPlayback()
            handler.removeMessages(0)
            handler.post(runnable)
        }


        layoutBinding.button.setOnClickListener {
            if (!layoutBinding.video.isPlaying) {
                val runnable = Runnable {
                    start = System.nanoTime()
                    layoutBinding.video.start()

                    layoutBinding.video.requestFocus()
                    //handlerThread.start()

                    //handler.post(runnable)
                    startTime = System.currentTimeMillis()

                }
                val service = Executors.newSingleThreadScheduledExecutor()
                val handle = service.scheduleAtFixedRate(runnable, 1, 122, TimeUnit.SECONDS)
                service.schedule({
                    handle.cancel(true)
                    service.shutdownNow()
                }, 122*20, TimeUnit.SECONDS)
            }




        }

        layoutBinding.video.setOnCompletionListener {
            val time:Long = (System.nanoTime()- start)/1000
            println("$time 微秒")
            handler.removeMessages(0)
            isCompleted = true
            times.add(time)
            println(times)
            var sum: Long = 0
            for (num in times) {
                sum += num
            }
            val average: Long = sum.toLong() / times.size
            println("client3当前平均值" + average + "us")
            //layoutBinding.text1.text = times.toString()+"当前平均值"+average+"us"



        }

        layoutBinding.pause.setOnClickListener {
            layoutBinding.video.pause()
            handler.removeMessages(0)
        }



        MessageManager.setListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_VIDEO && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                layoutBinding.video.setVideoURI(uri)
                layoutBinding.video.start()
                layoutBinding.video.requestFocus()
            }
        }
    }

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 -> {
                    val time = System.currentTimeMillis() - startTime
                    layoutBinding.text.text = time.toString()
                    Log.d("TAG", "handleMessage:${time.toString()} ")
                }

            }
            super.handleMessage(msg)
        }
    }

    override fun handleMessage() {
        if (!layoutBinding.video.isPlaying) {

            layoutBinding.video.start()
            layoutBinding.video.requestFocus()
            //handlerThread.start()

            handler.post(runnable)
            startTime = System.currentTimeMillis()
        }
    }


    /**
     * 广播接受
     */
    fun onBroadcastReceive() {
        Thread {
            try {
                // 创建接收数据报套接字并将其绑定到本地主机上的指定端口
                val datagramSocket = DatagramSocket(BROADCAST_PORT)
                while (isRuning) {
                    val buf = ByteArray(1024)
                    val datagramPacket = DatagramPacket(buf, buf.size)
                    datagramSocket.receive(datagramPacket)
                    serverHost = datagramPacket.address.hostAddress
//                    val message = String(datagramPacket.data, 0, datagramPacket.length)
//                    println(message)
//                    onBroadcastSend(message)
                    handleMessage()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun onBroadcastSend(message:String) {
        Thread {
            try {
                val inetAddress = InetAddress.getByName(BROADCAST_IP)
                val datagramSocketSend = DatagramSocket()
                val data: ByteArray = "client3".toByteArray()
                val datagramPacket =
                    DatagramPacket(data, data.size, inetAddress, 8888)
                datagramSocketSend.send(datagramPacket)
                // 发送设置为广播
                datagramSocketSend.broadcast = true
                datagramSocketSend.close()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}
