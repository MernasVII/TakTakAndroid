package tn.esprit.taktakandroid.uis.common

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import tn.esprit.taktakandroid.R

class LoginActivity : AppCompatActivity() {

    private lateinit var btnLogin:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin=findViewById(R.id.btn_login)

        btnLogin.setOnClickListener{
            var intent= Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}