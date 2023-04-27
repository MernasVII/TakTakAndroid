package tn.esprit.taktakandroid.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import okhttp3.internal.notify
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.utils.Constants.CHANNEL_ID
import tn.esprit.taktakandroid.utils.Constants.CHANNEL_NAME
import tn.esprit.taktakandroid.utils.Constants.NOTIFICATION_ID


object MyNotificationManager {
    private val vibrationPattern = longArrayOf(500, 500, 500, 500)
    var alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


    fun sendNotif(context: Context,  message: String) {
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotifChannel(manager)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("TakTak")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setVibrate(vibrationPattern)
            .setSound(alarmSound)

        // Show the notification
        manager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun createNotifChannel(manager:NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                lightColor = Color.BLUE
                enableLights(true)
            }
            manager.createNotificationChannel(channel)
        }
    }
}