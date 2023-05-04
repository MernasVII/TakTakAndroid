package tn.esprit.taktakandroid.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.utils.Constants.CHANNEL_ID
import tn.esprit.taktakandroid.utils.Constants.CHANNEL_NAME
import java.io.IOException


const val TAG = "SocketService"

class SocketService : Service() {

    companion object {
        private lateinit var _mSocket: Socket
        fun sendMessage(msg: String) {
            _mSocket.emit("NodeJS Server Port", msg)
        }
    }

    override fun onCreate() {
        super.onCreate()
        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                importance
            )
            channel.enableVibration(false)
            channel.setSound(null, null)
            channel.enableLights(false)

            manager.createNotificationChannel(channel)
        }
      /*  val notificationIntent = Intent(this, HomeActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, 0
        )
*/
        var notification: Notification = NotificationCompat.Builder(
            applicationContext,
            CHANNEL_ID
        )
            .setContentTitle(
                applicationContext.getString(tn.esprit.taktakandroid.R.string.app_name)
            )
            .setContentText("")
            .setSmallIcon(R.drawable.logo)
            .setSound(null)
            .setVibrate(null)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            //.setContentIntent(pendingIntent)
            .build()


        startForeground(1337, notification)

        try {
            _mSocket = IO.socket(Constants.SOCKET_URL)
            _mSocket.connect()
            // sendMessage("643585058d323d36598694b6/ canceled an appointment!/643585058d323d36598694b6")

        } catch (e: IOException) {
            Log.d(TAG, e.toString())
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CoroutineScope(Dispatchers.IO).launch {
            AppDataStore.init(applicationContext)
            val currUserID = AppDataStore.readString(Constants.USER_ID)
            _mSocket.on(currUserID) { data ->
                val msgReceived = JSONObject(data[0].toString()).getString("msg")
                Log.d(TAG, msgReceived)
                    MyNotificationManager.sendNotif(applicationContext, msgReceived)

            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "Socket closed")
        try {
            _mSocket.close()
        } catch (e: IOException) {
            Log.d(TAG, e.toString())
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}