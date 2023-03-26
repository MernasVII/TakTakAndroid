package tn.esprit.taktakandroid.uis.common

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var mainView: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mainView = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mainView.root)
        mainView.btnLogin.setOnClickListener {
            doLogin()
        }
    }

    private fun doLogin() {
        var intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()

    }
}