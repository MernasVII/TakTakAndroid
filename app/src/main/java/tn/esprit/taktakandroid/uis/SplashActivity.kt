package tn.esprit.taktakandroid.uis

import android.app.Activity
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
import java.util.*

class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        AppDataStore.init(applicationContext)
        lifecycleScope.launch(Dispatchers.IO) {


            val langStored = AppDataStore.readString("LANG")

            if (!langStored.isNullOrEmpty()) {
                if (langStored == "en") {
                    setLocal(this@SplashActivity, "en")
                } else {
                    setLocal(this@SplashActivity, "fr")
                }
            } else {
                setLocal(this@SplashActivity, "en")
            }


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

    private fun setLocal(activity: Activity, langCode: String) {
        val customLocale = Locale(langCode)
        Locale.setDefault(customLocale)
        val resources = activity.resources
        val config = resources.configuration
        config.setLocale(customLocale)
        resources.updateConfiguration(config, resources.displayMetrics)

    }


}