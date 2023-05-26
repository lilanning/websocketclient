package com.example.socketclient

import android.R.layout
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.WindowManager
import android.widget.MediaController
import androidx.activity.ComponentActivity
import androidx.databinding.DataBindingUtil
import com.example.socketclient.databinding.LayoutBinding
import com.example.socketclient.ui.MyPresentation
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity(),MessageListener {
    private lateinit var layoutBinding: LayoutBinding
    private lateinit var webSocketClient:MyWebSocketClient
    private var startTime = System.currentTimeMillis()
    private var start = System.nanoTime()
    private val times = ArrayList<Long>()


    lateinit var myPresentation:MyPresentation

    private lateinit var mediaPlayer:MediaPlayer

    private val REQUEST_CODE_SELECT_VIDEO = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        layoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.layout, null, false)
        setContentView(layoutBinding.root)
        //设置无导航栏
        val options= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
        window.decorView.systemUiVisibility = options
        layoutBinding.start.setOnClickListener {
            layoutBinding.line1.visibility = View.GONE
            val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            val displays = displayManager.displays
            if (displays.size>1){
                myPresentation = MyPresentation(this,displays[1])
                myPresentation.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                myPresentation.show()
                println("展示副屏")
            }
            Thread{
                try {
                     webSocketClient = MyWebSocketClient(this,"ws://169.254.228.156:8888")
                    Log.d("TAG", "onCreate: "+webSocketClient.readyState)
//                    while (!webSocketClient.readyState.equals(WebSocket.READYSTATE.OPEN)){
//                        println("还没有打开呢")
//                    }
                    webSocketClient.connectBlocking()
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

//        val packageName = packageName
//        val uri = Uri.parse("android.resource://$packageName/" + R.raw.aaoutput)
//        Log.d("MainActivity", "onCreate: ${uri.toString()}")
//
//        layoutBinding.video.setVideoURI(uri)
//        val mediaController = MediaController(this)
//        layoutBinding.video.setMediaController(mediaController)
//        layoutBinding.select.setOnClickListener {
//            val intent = Intent(Intent.ACTION_GET_CONTENT)
//            intent.type = "video/*"
//            startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO)
//            layoutBinding.video.stopPlayback()
//            handler.removeMessages(0)
//            handler.post(runnable)
//        }


        layoutBinding.button.setOnClickListener {
            if (!mediaPlayer.isPlaying) {
                val runnable = Runnable {
                    start = System.nanoTime()
                    mediaPlayer.start()
                    //handlerThread.start()

                    //handler.post(runnable)
                    startTime = System.currentTimeMillis()

                }
                val service = Executors.newSingleThreadScheduledExecutor()
                val handle = service.scheduleAtFixedRate(runnable, 1000, 1000, TimeUnit.MILLISECONDS)
                service.schedule({
                    handle.cancel(true)
                    service.shutdownNow()
                }, 70*1000, TimeUnit.MILLISECONDS)
            }




        }

        mediaPlayer = MediaPlayer.create(this,R.raw.ccoutput)
        mediaPlayer.setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MOVIE).build())
        val surfaceHolder = layoutBinding.video.holder
        surfaceHolder.addCallback(object : SurfaceHolder.Callback{
            override fun surfaceCreated(holder: SurfaceHolder) {
                mediaPlayer.setDisplay(surfaceHolder)
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }

        })



        MessageManager.setListener(this)

        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, 1234)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SELECT_VIDEO && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
//                layoutBinding.video.setVideoURI(uri)
//                layoutBinding.video.start()
//                layoutBinding.video.requestFocus()
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

    var first = true
    override fun handleMessage() {
        if (!mediaPlayer.isPlaying) {

            mediaPlayer.start()
            layoutBinding.video.requestFocus()
            myPresentation.play()


            //主控制器客户端必有

//            while(first){
//                first = false
//                println("1111")
//                val runnable = Runnable {
//                    webSocketClient.send(mediaPlayer.currentPosition.toString())
//                    webSocketClient.setStartTime(System.currentTimeMillis())
//                    print("当前进度${mediaPlayer.currentPosition}")
//
//                }
//                val service = Executors.newSingleThreadScheduledExecutor()
//                val handle = service.scheduleAtFixedRate(runnable, 1000, 1000, TimeUnit.MILLISECONDS)
//                service.schedule({
//                    handle.cancel(true)
//                    service.shutdownNow()
//                    first = true
//                }, 70*1000, TimeUnit.MILLISECONDS)
//            }

        }
    }

    override fun seekTo(mes: String) {
        if (!mediaPlayer.isPlaying){
            mediaPlayer.start()
            layoutBinding.video.requestFocus()
            myPresentation.play()
        }
        val time = mes.toInt()+4 - mediaPlayer.currentPosition
        println("相差$time")

        if (time>40){
            println("修正$time")
            mediaPlayer.seekTo(mes.toLong(),MediaPlayer.SEEK_NEXT_SYNC)
            myPresentation.seekTo(mes.toLong(),MediaPlayer.SEEK_NEXT_SYNC)
        } else if (time<-40){
            println("修正$time")
            mediaPlayer.seekTo(mes.toLong(),MediaPlayer.SEEK_PREVIOUS_SYNC)
            myPresentation.seekTo(mes.toLong(),MediaPlayer.SEEK_PREVIOUS_SYNC)
        }

    }




}
