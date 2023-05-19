package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.MainactivityBinding
import com.example.myapplication.databinding.SecondBinding
import com.example.myapplication.ui.MyPresentation
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.Executors


class MainActivity : ComponentActivity() {

    private val TAG = MainActivity::class.java.simpleName
    var times: ArrayList<Long> = ArrayList()
    private lateinit var mainactivityBinding: MainactivityBinding
    var executorService = Executors.newFixedThreadPool(3)

    private val BROADCAST_IP = "255.255.255.255"
    private val BROADCAST_PORT = 8886

    val SEND_PORT: Int = 8008
    val DEST_PORT: Int = 8009
    var isClosed: Boolean = false
    val LOCAL_PORT:Int = 8009
    var sendBuffer: String = "This is UDP Server"
    private val isRuning = true
    private var serverHost = ""
    var startTime = System.currentTimeMillis()
    private var shouldStop = 0

    lateinit var mUDPBroadCast: UDPBroadcaster
    lateinit var myPresentation:MyPresentation



    private val layoutParams = WindowManager.LayoutParams().apply {
        width = WRAP_CONTENT
        height = WRAP_CONTENT
        gravity = Gravity.TOP or Gravity.END
        flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    }

    var secondScreen = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainactivityBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.mainactivity, null, false)
        setContentView(mainactivityBinding.root)
//        val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
//        val displays = displayManager.displays
//        if (displays.size>1){
//             myPresentation = MyPresentation(this,displays[1])
//            myPresentation.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
//            myPresentation.show()
//        }

        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivityForResult(intent, 1)
            } else {

            }
        }
        onBroadcastReceive()
        mUDPBroadCast = UDPBroadcaster(this)

//        mainactivityBinding.btnSend.setOnClickListener {
//            if (displays.size>1){
//                val myPresentation = MyPresentation(this,displays[1])
//                myPresentation.window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
//                myPresentation.show()
//            }
           // addSecond()
            //onBroadcastSend(it)
//            val runnable = Runnable {
//                startTime = System.currentTimeMillis()
//                onBroadcastSend(it)
//
//                shouldStop++
//            }
//            val service = Executors.newSingleThreadScheduledExecutor()
//            val handle = service.scheduleAtFixedRate(runnable, 1, 2, TimeUnit.SECONDS)
//            service.schedule({
//                handle.cancel(true)
//                service.shutdownNow()
//            }, 2000, TimeUnit.SECONDS)
//        }
//        mainactivityBinding.btnReceive.setOnClickListener {
//            onBroadcastReceive()
//        }

//        val options= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN
//        window.decorView.systemUiVisibility = options
//        val packageName = packageName
//        val uri = Uri.parse("android.resource://$packageName/" + R.raw.aaoutput)
//        Log.d("MainActivity", "onCreate: ${uri.toString()}")
//
//        mainactivityBinding.video.setVideoURI(uri)
//        val mediaController = MediaController(this)
//        mainactivityBinding.video.setMediaController(mediaController)
//
//        val start = System.nanoTime()
        mainactivityBinding.play.setOnClickListener {
//            mainactivityBinding.video.start()
//            mainactivityBinding.video.requestFocus()
//            println("主屏幕开始时间 ${start.toString()}")
//            myPresentation.play()
            onBroadcastSend(it)

        }
//        mainactivityBinding.video.setOnCompletionListener {
//            println("主屏幕完成时间 ${System.nanoTime()}")
//        }
    }

    fun onBroadcastSend(view: View?) {
        Thread {
            try {
                val inetAddress = InetAddress.getByName(BROADCAST_IP)
                val datagramSocketSend = DatagramSocket()
                val data: ByteArray = "1111".toByteArray()
                val datagramPacket =
                    DatagramPacket(data, data.size, inetAddress, BROADCAST_PORT)
                datagramSocketSend.send(datagramPacket)
                // 发送设置为广播
                datagramSocketSend.broadcast = true
                datagramSocketSend.close()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    /**
     * 广播接受
     */
    fun onBroadcastReceive() {
        Thread {
            try {
                // 创建接收数据报套接字并将其绑定到本地主机上的指定端口
                val datagramSocket = DatagramSocket(8888)
                while (isRuning) {
                    val buf = ByteArray(1024)
                    val datagramPacket = DatagramPacket(buf, buf.size)
                    datagramSocket.receive(datagramPacket)
                    val message = String(datagramPacket.data, 0, datagramPacket.length)
                    println(message)
                    executorService.submit { excute(message) }

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    fun excute( message:String){
        val current = System.currentTimeMillis()
        val perTime: Long = current - startTime
        Log.i("TAG", "OnMessage: " + message + "perTime:" + perTime + "ms")
        when(message){
            "client1"->{
                handle1(perTime)
            }
            "client2"->{
                handle2(perTime)
            }
            "client3"->{
                handle3(perTime)
            }
        }
    }


    var times1: ArrayList<Long> = ArrayList<Long>()
    var times2: ArrayList<Long> = ArrayList<Long>()
    var times3: ArrayList<Long> = ArrayList<Long>()
    fun handle1(time: Long) {
        println(time)
        times1.add(time)
        println("client1数据  $times1")
        var sum: Long = 0
        for (num in times1) {
            sum += num
        }
        val average: Double = sum.toDouble() / times1.size
        println("client1当前平均值   " + average + "ms")
    }
    fun handle2(time: Long) {
        println(time)
        times2.add(time)
        println("client2数据  $times2")
        var sum: Long = 0
        for (num in times2) {
            sum += num
        }
        val average = sum.toDouble() / times2.size
        println("client2当前平均值    " + average + "ms")
    }

    fun handle3(time: Long) {
        times3.add(time)
        println("client3数据  $times3")
        var sum: Long = 0
        for (num in times3) {
            sum += num
        }
        val average = sum.toDouble() / times3.size
        println("client3当前平均值    " + average + "ms")
    }






    fun GetPermission() {
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

    private fun updateVrLayout(view: View, layoutParams: WindowManager.LayoutParams) {
        Log.i(TAG, "updateVrLayout")
         val windowManager: WindowManager = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        try {
            if (view.parent != null || view.isAttachedToWindow) {
                windowManager.updateViewLayout(view, layoutParams)
            } else {
                windowManager.addView(view, layoutParams)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i(TAG, "error ${e.message}")
        }
    }

    override fun onResume() {
        super.onResume()
        val secondBinding = DataBindingUtil.inflate<SecondBinding>(layoutInflater,R.layout.second,null,false)
        mainactivityBinding.play.postDelayed({
            updateVrLayout(secondBinding.root,layoutParams)
        },1000)
        secondBinding.play.setOnClickListener {
            onBroadcastSend(it)
        }
    }
}
