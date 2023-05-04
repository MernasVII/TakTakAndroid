package tn.esprit.taktakandroid.uis

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
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
import kotlin.math.log

class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppDataStore.init(applicationContext)
        setTheme()
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

    private fun setTheme() {
        var isDarkThemeSet:Boolean
        lifecycleScope.launch(Dispatchers.Main) {
            isDarkThemeSet = AppDataStore.readBool(Constants.DARK_THEME_SET)  ?: false
            if(isDarkThemeSet){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            Log.d("TESTINGG", "setTheme: $isDarkThemeSet")
        }
    }

    private fun getLastSignedInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


}