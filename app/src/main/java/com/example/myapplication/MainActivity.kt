package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.MainactivityBinding
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {

    private val TAG = MainActivity::class.java.simpleName
    var times: ArrayList<Long> = ArrayList()
    private lateinit var mainactivityBinding: MainactivityBinding
    var executorService = Executors.newFixedThreadPool(3)

    private val BROADCAST_IP = "255.255.255.255"
    private val BROADCAST_PORT = 8887

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainactivityBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.mainactivity, null, false)
        setContentView(mainactivityBinding.root)
        onBroadcastReceive()
        mUDPBroadCast = UDPBroadcaster(this)

        mainactivityBinding.btnSend.setOnClickListener {
            onBroadcastSend(it)
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
        }
        mainactivityBinding.btnReceive.setOnClickListener {
            onBroadcastReceive()
        }

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










}
