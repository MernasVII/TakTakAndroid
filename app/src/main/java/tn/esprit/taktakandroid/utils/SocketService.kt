package tn.esprit.taktakandroid.utils

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
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
            val currUserID=AppDataStore.readString(Constants.USER_ID)
            _mSocket.on(currUserID) { data ->
                val msgReceived= JSONObject(data[0].toString()).getString("msg")
                Log.d(TAG, msgReceived)
                MyNotificationManager.sendNotif(applicationContext,msgReceived)
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