package com.example.orientationsensorretriever

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList

class GetOrientationInfoService: Service(), OrientationInfoProvider.Listener {

    /**
     * This is a list of callbacks that have been registered with the service.
     */
    private val rotationInfoServiceCallbacks: RemoteCallbackList<IOrientationInfoCallback> =
        RemoteCallbackList()

    private lateinit var orientationInfoProvider: OrientationInfoProvider

    override fun onBind(intent: Intent?): IBinder? {
        return intent?.let {
            startForeground(1, NotificationManager.getNotification(application))
            RotationInfoServiceBinder()
        }
    }

    override fun onCreate() {
        super.onCreate()
        orientationInfoProvider = OrientationInfoProvider.getInstance(applicationContext)
        orientationInfoProvider.startListening(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        orientationInfoProvider.stopListening()
        rotationInfoServiceCallbacks.kill()
    }

    inner class RotationInfoServiceBinder: IOrientationInfoService.Stub(){

        override fun registerCallBack(rotationInfoCallBack: IOrientationInfoCallback?) {
            rotationInfoCallBack?.let { rotationInfoServiceCallbacks.register(it) }
        }

        override fun unRegisterCallBack(rotationInfoCallBack: IOrientationInfoCallback?) {
            rotationInfoCallBack?.let { rotationInfoServiceCallbacks.unregister(it) }
        }

    }

    private fun broadcastToClients(orientation: FloatArray) {
        val count = rotationInfoServiceCallbacks.beginBroadcast()
        for (index in 0 until count) {
            rotationInfoServiceCallbacks.getBroadcastItem(index).onResponse(orientation)
        }
        rotationInfoServiceCallbacks.finishBroadcast()
    }

    override fun onOrientationChanged(orientation: FloatArray) {
        broadcastToClients(orientation)
    }
}