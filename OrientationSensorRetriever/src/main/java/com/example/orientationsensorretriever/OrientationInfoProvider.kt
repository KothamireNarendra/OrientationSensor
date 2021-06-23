package com.example.orientationsensorretriever

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

internal class OrientationInfoProvider private constructor(context: Context): SensorEventListener {

    interface Listener {
        fun onOrientationChanged(orientation: FloatArray)
    }

    companion object{

        private val TAG = OrientationInfoProvider::class.java.simpleName

        private val SENSOR_DELAY_MICROS = 8 * 1000 // 16ms

        private var instance: OrientationInfoProvider? = null

        private val lock = Any()

        @JvmStatic
        fun getInstance(context: Context): OrientationInfoProvider {

            return synchronized(lock){
                if(instance == null){
                    instance = OrientationInfoProvider(context)
                }
                instance!!
            }
        }
    }

    private var rotationListener: Listener? = null

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private var lastAccuracy = 0

    /**
     * Function to setup listeners for #SensorManager
     */
    fun startListening(listener: Listener){
        if(rotationListener == listener) return
        rotationListener = listener

        sensorManager.registerListener(this, rotationSensor, SENSOR_DELAY_MICROS)
    }

    /**
     * Function to clean up listeners
     */
    fun stopListening(){
        sensorManager.unregisterListener(this)
        rotationListener = null
    }

    /**
     * Callback function for receiving sensor events
     */
    override fun onSensorChanged(sensorEvent: SensorEvent) {
        Log.d(TAG, "onSensorChanged called")
        rotationListener?.let {
            if(lastAccuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) return@let

            if(sensorEvent.sensor == rotationSensor) onOrientationChange(sensorEvent.values)
        }
    }

    /**
     * Callback function for receiving sensor accuracy
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        lastAccuracy = accuracy
    }

    private fun onOrientationChange(rotationVector: FloatArray) {
        val rotationMatrix = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector)

        // Transform rotation matrix into azimuth/pitch/roll
        val orientation = FloatArray(3)
        SensorManager.getOrientation(rotationMatrix, orientation)

        val apr = orientation.map { Math.toDegrees(it.toDouble()).toFloat() }

        rotationListener?.onOrientationChanged(apr.toFloatArray())
    }
}