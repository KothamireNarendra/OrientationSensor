package com.example.orientationsensorretriever

import android.content.Context
import java.util.concurrent.CopyOnWriteArraySet

class OrientationSensorManager private constructor(context: Context): OrientationSensor {

    companion object{

        private var instance: OrientationSensorManager? = null

        private val lock = Any()

        @JvmStatic
        fun getInstance(context: Context): OrientationSensorManager {

            return synchronized(lock){
                if(instance == null){
                    instance = OrientationSensorManager(context)
                }
                instance!!
            }
        }
    }

    private val serviceConnector = OrientationInfoServiceConnector(context).also {
        it.callback = object : OrientationSensor.Callback {
            override fun onResponse(rotation: FloatArray) {
                callbacks.forEach { call -> call.onResponse(rotation) }
            }
        }
    }

    private val callbacks = CopyOnWriteArraySet<OrientationSensor.Callback>()

    override fun registerListener(callback: OrientationSensor.Callback){
        callbacks.add(callback)
        if(callbacks.size > 0) connect()
    }

    override fun unRegisterListener(callback: OrientationSensor.Callback){
        callbacks.remove(callback)
        if(callbacks.isEmpty()) disconnect()
    }

    private fun connect(){
        serviceConnector.connectService()
    }

    private fun disconnect() {
        serviceConnector.disconnectService()
    }
}