package tn.esprit.taktakandroid.uis.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.ActivityEmailForgotPwdBinding

class EmailForgotPwdActivity : AppCompatActivity() {
    private lateinit var mainView:ActivityEmailForgotPwdBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainView=ActivityEmailForgotPwdBinding.inflate(layoutInflater)
        setContentView(mainView.root)
    }
}