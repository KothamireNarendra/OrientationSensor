package com.example.rotationsensor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.orientationsensorretriever.OrientationSensor
import com.example.orientationsensorretriever.OrientationSensorManager

class MainActivity : AppCompatActivity(), OrientationSensor.Callback {

    private lateinit var orientationSensorManager: OrientationSensorManager
    private lateinit var rotationTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rotationTv = findViewById(R.id.rotationTv)
        orientationSensorManager = OrientationSensorManager.getInstance(applicationContext)
    }

    override fun onStart() {
        super.onStart()
        orientationSensorManager.registerListener(this)
    }

    override fun onStop() {
        super.onStop()
        orientationSensorManager.unRegisterListener(this)
    }

    override fun onResponse(rotation: FloatArray) {
        val s = "rotation x - ${rotation[0]} y - ${rotation[1]} z - ${rotation[2]}"
        rotationTv.text = s
        Log.d("Narendra", s)
    }
}