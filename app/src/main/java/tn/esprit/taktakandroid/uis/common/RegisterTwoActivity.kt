package tn.esprit.taktakandroid.uis.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.ActivityRegisterTwoBinding

class RegisterTwoActivity : AppCompatActivity() {
    private lateinit var mainView: ActivityRegisterTwoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainView= ActivityRegisterTwoBinding.inflate(layoutInflater)
        setContentView(mainView.root)
    }
}