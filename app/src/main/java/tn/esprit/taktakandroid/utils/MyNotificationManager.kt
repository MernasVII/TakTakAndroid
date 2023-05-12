package tn.esprit.taktakandroid.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.uis.home.HomeActivity
import tn.esprit.taktakandroid.utils.Constants.CHANNEL_ID
import tn.esprit.taktakandroid.utils.Constants.CHANNEL_NAME


object MyNotificationManager {
    private val vibrationPattern = longArrayOf(500, 500, 500, 500)
    private var alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)


    fun sendNotif(context: Context, message: String) {

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotifChannel(manager)

        val intent = Intent(context, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("showNotif", true)
        intent.action = "showNotif"


        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            FLAG_IMMUTABLE
        )

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.logo)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    context.resources,
                    R.mipmap.ic_logo
                )
            )
            .setContentIntent(pendingIntent)
            .setContentTitle(
                context.applicationContext.getString(tn.esprit.taktakandroid.R.string.app_name)
            )
            .setAutoCancel(true)
            .setContentText(message)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(vibrationPattern)
            .setSound(alarmSound)

        manager.notify(NotifIDGenerator.generateNotifID(), builder.build())
    }


    private fun createNotifChannel(manager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                importance
            )
            channel.enableVibration(true)
            channel.vibrationPattern = vibrationPattern
            channel.setSound(alarmSound, null)

            channel.enableLights(true)

            manager.createNotificationChannel(channel)
        }
    }

}