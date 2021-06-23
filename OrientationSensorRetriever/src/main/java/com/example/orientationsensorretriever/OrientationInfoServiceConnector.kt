package com.example.orientationsensorretriever

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.RemoteException
import android.util.Log

/**
 * This is a class that binds to [GetOrientationInfoService] and receive orientation values
 */
internal class OrientationInfoServiceConnector(private val context: Context) {

    companion object {
        val TAG: String = OrientationInfoServiceConnector::class.java.simpleName
    }

    private var orientationInfoService: IOrientationInfoService? = null
    internal var callback: OrientationSensor.Callback? = null
    var serviceConnected = false

    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG, "onServiceConnected")
            serviceConnected = true
            orientationInfoService = IOrientationInfoService.Stub.asInterface(service)
            try {
                orientationInfoService?.registerCallBack(rotationInfoServiceCallback)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected")
            serviceConnected = false
            orientationInfoService = null
        }
    }

    private val rotationInfoServiceCallback = object : IOrientationInfoCallback.Stub() {

        override fun onResponse(rotation: FloatArray?) {
            Log.d(TAG, "rotation is got ${callback != null}")
            callback?.onResponse(rotation ?: FloatArray(0))
        }
    }

    /**
     * Function to bind to [GetOrientationInfoService]
     */
    fun connectService() {
        if (serviceConnected) {
            Log.d(TAG, "connectService: service was already connected. Ignoring...")
            return
        }
        val intent = Intent(context, GetOrientationInfoService::class.java)
        /*intent.component = ComponentName.unflattenFromString(
            context.getString(R.string.component_service)
        )
        intent.action = context.getString(R.string.action_service)*/
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    /**
     * Function to unregister [IRotationInfoCallback] and unbind [GetOrientationInfoService]
     */
    fun disconnectService() {
        if (!serviceConnected) {
            Log.d(TAG, "disconnectService: service was not connected. Ignoring...")
            return
        }
        try {
            orientationInfoService?.unRegisterCallBack(rotationInfoServiceCallback)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
        context.unbindService(serviceConnection)
        serviceConnected = false
    }
}