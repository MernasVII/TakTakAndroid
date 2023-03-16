package tn.esprit.taktakandroid.uis.common

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.ActivityOtpactivityBinding
import tn.esprit.taktakandroid.utils.OtpOnKeyListener
import tn.esprit.taktakandroid.utils.OtpTextWatcher

class OTPActivity : AppCompatActivity() {
    private lateinit var mainView:ActivityOtpactivityBinding
    private lateinit var editTexts: List<EditText>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainView=ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(mainView.root)

        editTexts =
            listOf(mainView.etDigit1, mainView.etDigit2, mainView.etDigit3, mainView.etDigit4)

        initOTPFields()

        mainView.btnVerifyOtp.setOnClickListener {
            finish()
            startActivity(Intent(this, ResetPwdActivity::class.java))
        }



    }
    private fun initOTPFields(){
        editTexts.forEachIndexed { index, editText ->
            editText.addTextChangedListener(OtpTextWatcher(index, editTexts))
            editText.setOnKeyListener(OtpOnKeyListener(index, editTexts))
        }

        mainView.etDigit1.setOnLongClickListener {
            onPaste(mainView.etDigit1)
            true
        }
    }
    private fun onPaste(editText: EditText) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboard.primaryClip ?: return
        val text = clipData.getItemAt(0).coerceToText(this).toString()
        if (text.length == 4) {
            editTexts.forEachIndexed { index, et ->
                et.setText(text[index].toString())
            }
            editText.clearFocus()
        }
    }
}