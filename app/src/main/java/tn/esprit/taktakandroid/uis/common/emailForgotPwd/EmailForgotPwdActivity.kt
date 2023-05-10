package tn.esprit.taktakandroid.uis.common.emailForgotPwd

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import render.animations.Attention
import render.animations.Render
import tn.esprit.taktakandroid.databinding.ActivityEmailForgotPwdBinding
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.uis.BaseActivity
import tn.esprit.taktakandroid.uis.common.otpVerification.OTPActivity
import tn.esprit.taktakandroid.utils.Resource

const val TAG = "EmailForgotPwdActivity"
class EmailForgotPwdActivity : BaseActivity() {
    private lateinit var mainView:ActivityEmailForgotPwdBinding
    private lateinit var viewModel: EmailForgotPwdViewModel
    private lateinit var render: Render

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainView=ActivityEmailForgotPwdBinding.inflate(layoutInflater)
        setContentView(mainView.root)
        render=Render(this)

        val userRepository = UserRepository()
        val viewModelProviderFactory = EmailForgotPwdViewModelProviderFactory(userRepository)

        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[EmailForgotPwdViewModel::class.java]

        mainView.etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setEmail(s.toString().trim().lowercase())
                viewModel.removeEmailError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        viewModel.emailError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlEmail.apply {
                    error = viewModel.emailError.value
                    isErrorEnabled = true
                    render.setAnimation(Attention.Shake(mainView.tlEmail))
                    render.start()
                }
            } else {
                mainView.tlEmail.apply {
                    isErrorEnabled = false
                }
            }
        }

        viewModel.sendOtpResult.observe(this@EmailForgotPwdActivity, Observer { result ->
            when (result) {
                is Resource.Success -> {
                    progressBarVisibility(false,mainView.progressBar)
                    result.data?.let {
                        Intent(this, OTPActivity::class.java).also {
                            it.putExtra("email",viewModel.email.value.toString())
                            finishOneActivityCallback.launch(it)
                        }
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false,mainView.progressBar)
                    result.message?.let { msg ->
                        showDialog(msg)
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true,mainView.progressBar)
                }
            }
        })

        mainView.btnSendEmail.setOnClickListener {
            viewModel.sendOtp()
        }

    }




}