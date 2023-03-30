package tn.esprit.taktakandroid.uis.common.otpVerification

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.databinding.ActivityOtpactivityBinding
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.uis.common.BaseActivity
import tn.esprit.taktakandroid.uis.common.resetPwd.ResetPwdActivity
import tn.esprit.taktakandroid.utils.OtpOnKeyListener
import tn.esprit.taktakandroid.utils.OtpTextWatcher
import tn.esprit.taktakandroid.utils.Resource


const val TAG = "OTPActivity"

class OTPActivity : BaseActivity() {
    private lateinit var mainView:ActivityOtpactivityBinding
    private lateinit var editTexts: List<EditText>
    private lateinit var viewModel: OtpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainView=ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(mainView.root)

        editTexts =
            listOf(mainView.etDigit1, mainView.etDigit2, mainView.etDigit3, mainView.etDigit4)

        initOTPFields()

        val userRepository = UserRepository()
        val viewModelProviderFactory = OtpViewModelProviderFactory(userRepository)

        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[OtpViewModel::class.java]


        intent.getStringExtra("email")?.let {
            viewModel.setEmail(it)
        }


        mainView.btnVerifyOtp.setOnClickListener {
            viewModel.setOtp(getOtpEntred())
            viewModel.verifyOtp()
        }

        mainView.tvResendOTP.setOnClickListener {
            viewModel.sendOtp()
        }

        viewModel.otpError.observe(this@OTPActivity){
            if (it.isNotEmpty()) {
                showDialog(it)
            }
        }
        viewModel.sendOtpResult.observe(this@OTPActivity) { result ->
            when (result) {
                is Resource.Success -> {
                    progressBarVisibility(false,mainView.progressBar)
                    result.data?.let {
                        showSnackbar(it.message,mainView.cl)
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false,mainView.progressBar)
                    result.message?.let { msg ->
                        showSnackbar(msg,mainView.cl)
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true,mainView.progressBar)
                }
            }
        }

        viewModel.verifyOtpResult.observe(this@OTPActivity){result->
            when (result) {
                is Resource.Success -> {
                    progressBarVisibility(false,mainView.progressBar)
                    Intent(this, ResetPwdActivity::class.java).also {
                        it.putExtra("email",viewModel.email.value.toString())
                        finishTwoActivitesCallback2.launch(it)
                    }

                }
                is Resource.Error -> {
                    progressBarVisibility(false,mainView.progressBar)
                    result.message?.let {
                        showDialog(it)
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true,mainView.progressBar)
                }
            }

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
    private fun getOtpEntred():String{
        var otp =""
        editTexts.forEachIndexed { index, _ ->
            otp += editTexts[index].text.toString()
        }
        return otp
    }
}