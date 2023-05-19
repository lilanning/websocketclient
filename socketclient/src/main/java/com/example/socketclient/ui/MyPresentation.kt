package com.example.socketclient.ui

import android.app.Presentation
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.widget.MediaController
import androidx.databinding.DataBindingUtil
import com.example.socketclient.R
import com.example.socketclient.databinding.SecondBinding

class MyPresentation(context:Context,display: Display):Presentation(context,display) {

    private lateinit var secondBinding: SecondBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        secondBinding = DataBindingUtil.inflate(layoutInflater, R.layout.second,null,false)
        setContentView(secondBinding.root)


        val packageName = context.packageName
        val uri = Uri.parse("android.resource://$packageName/" + R.raw.bboutput)
        Log.d("MainActivity", "onCreate: ${uri.toString()}")

        secondBinding.video.setVideoURI(uri)
        val mediaController = MediaController(context)
        secondBinding.video.setMediaController(mediaController)
        secondBinding.video.setOnCompletionListener {
            println("副屏完成时间 ${System.nanoTime()}")
        }

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    fun play(){

        val start = System.nanoTime()
        secondBinding.video.start()
        println("副屏开始时间 $start")
        secondBinding.video.requestFocus()
    }
}