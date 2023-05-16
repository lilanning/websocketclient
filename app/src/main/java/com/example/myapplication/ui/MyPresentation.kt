package com.example.myapplication.ui

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.view.Display
import androidx.databinding.DataBindingUtil
import com.example.myapplication.R
import com.example.myapplication.databinding.SecondBinding

class MyPresentation(context:Context,display: Display):Presentation(context,display) {

    private lateinit var secondBinding: SecondBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        secondBinding = DataBindingUtil.inflate(layoutInflater, R.layout.second,null,false)
        setContentView(secondBinding.root)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }
}