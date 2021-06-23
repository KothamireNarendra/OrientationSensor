package com.example.orientationsensorretriever

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_HIGH


class NotificationManager {

    companion object{
        /**
         * The id of the channel for notifications.
         */
        private const val CHANNEL_ID = "orientation_info_service_channel_01"

        /**
         * The name of the channel for notifications.
         */
        private const val CHANNEL_NAME = "orientation_info_service_channel"

        /**
         * Returns the [NotificationCompat] used as part of the foreground service.
         */
        internal fun getNotification(context: Context): Notification {

            val channelId =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationChannel(context, CHANNEL_ID, CHANNEL_NAME)
                } else {
                    // If earlier version channel ID is not used
                    // https://developer.android.com/reference/android/support/v4/app/NotificationCompat.Builder.html#NotificationCompat.Builder(android.content.Context)
                    ""
                }

            val notificationBuilder = NotificationCompat.Builder(context, channelId )
            return notificationBuilder.setOngoing(true)
                .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                .setPriority(PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build()
        }

        /**
         * Returns notification channel id for android version 'O' and above
         */
        @RequiresApi(Build.VERSION_CODES.O)
        private fun createNotificationChannel(context: Context, channelId: String, channelName: String): String{
            val channel = NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE)
            channel.lightColor = Color.BLUE
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val service = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(channel)
            return channelId
        }
    }
}