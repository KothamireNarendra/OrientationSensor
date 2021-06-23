package com.example.orientationsensorretriever

interface OrientationSensor {

    fun registerListener(callback: Callback)

    fun unRegisterListener(callback: Callback)

    interface Callback{
        fun onResponse(rotation: FloatArray)
    }
}