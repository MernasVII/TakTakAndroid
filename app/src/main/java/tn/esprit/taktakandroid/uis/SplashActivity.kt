package tn.esprit.taktakandroid.uis

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tn.esprit.taktakandroid.uis.common.HomeActivity
import tn.esprit.taktakandroid.uis.common.login.LoginActivity
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants.AUTH_TOKEN

class SplashActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppDataStore.init(applicationContext)
        lifecycleScope.launch(Dispatchers.IO) {
            val token = AppDataStore.readString(AUTH_TOKEN) ?: ""
            if(token.isEmpty() && getLastSignedInAccount() == null){
                withContext(Dispatchers.Main){
                    Intent(applicationContext, LoginActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }

            }
            else{
                withContext(Dispatchers.Main){
                    Intent(applicationContext, HomeActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                }
            }
        }


    }
    private fun getLastSignedInAccount():GoogleSignInAccount?{
        return GoogleSignIn.getLastSignedInAccount(this)
    }
}