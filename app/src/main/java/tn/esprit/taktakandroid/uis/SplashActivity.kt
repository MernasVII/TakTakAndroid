package tn.esprit.taktakandroid.uis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tn.esprit.taktakandroid.uis.common.HomeActivity
import tn.esprit.taktakandroid.uis.common.login.LoginActivity
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants.AUTH_TOKEN

class SplashActivity : AppCompatActivity() {

    private lateinit var appDataStore: AppDataStore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDataStore= AppDataStore(this)
        lifecycleScope.launch(Dispatchers.IO) {
            val token = appDataStore.readString(AUTH_TOKEN) ?: ""
            if(token.isEmpty()){
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
}