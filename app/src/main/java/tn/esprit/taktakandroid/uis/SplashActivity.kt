package tn.esprit.taktakandroid.uis

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import tn.esprit.taktakandroid.uis.common.login.LoginActivity
import tn.esprit.taktakandroid.uis.home.HomeActivity
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Constants.AUTH_TOKEN
import tn.esprit.taktakandroid.utils.SocketService

class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        AppDataStore.init(applicationContext)
        lifecycleScope.launch(Dispatchers.IO) {
            val token = AppDataStore.readString(AUTH_TOKEN) ?: ""
            if (token.isEmpty() && getLastSignedInAccount() == null) {
                withContext(Dispatchers.Main) {
                    Intent(applicationContext, LoginActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }

            } else {

                withContext(Dispatchers.Main) {
                    Intent(applicationContext, HomeActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }
            }
        }


    }

    private fun getLastSignedInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(this)
    }


}