package tn.esprit.taktakandroid.uis.common

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.ActivityResetPwdBinding

class ResetPwdActivity : AppCompatActivity() {
    private lateinit var mainView:ActivityResetPwdBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainView=ActivityResetPwdBinding.inflate(layoutInflater)
        setContentView(mainView.root)
    }
}