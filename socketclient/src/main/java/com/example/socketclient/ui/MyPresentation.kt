package com.example.socketclient.ui

import android.app.Presentation
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.SurfaceHolder
import android.widget.MediaController
import androidx.databinding.DataBindingUtil
import com.example.socketclient.R
import com.example.socketclient.databinding.SecondBinding

class MyPresentation(context:Context,display: Display):Presentation(context,display) {

    private lateinit var secondBinding: SecondBinding
    private lateinit var mediaPlayer:MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        secondBinding = DataBindingUtil.inflate(layoutInflater, R.layout.second,null,false)
        setContentView(secondBinding.root)


//        val packageName = context.packageName
//        val uri = Uri.parse("android.resource://$packageName/" + R.raw.ddoutput)
//        Log.d("MainActivity", "onCreate: ${uri.toString()}")
//
//        secondBinding.video.setVideoURI(uri)
//        val mediaController = MediaController(context)
//        secondBinding.video.setMediaController(mediaController)
//        secondBinding.video.setOnCompletionListener {
//            println("副屏完成时间 ${System.nanoTime()}")
//        }

        mediaPlayer = MediaPlayer.create(context,R.raw.ffoutput)
        mediaPlayer.setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MOVIE).build())
        val surfaceHolder = secondBinding.video.holder
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
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    fun play(){

        mediaPlayer.start()
    }

    fun seekTo(time: Long, mode:Int) {
        mediaPlayer.seekTo(time, mode)
    }
}