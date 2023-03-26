package tn.esprit.taktakandroid.uis.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.ActivityRegisterOneBinding

class RegisterOneActivity : AppCompatActivity() {
    private lateinit var mainView:ActivityRegisterOneBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainView=ActivityRegisterOneBinding.inflate(layoutInflater)
        setContentView(mainView.root)
    }
}