package tn.esprit.taktakandroid.uis.common.emailForgotPwd

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.databinding.ActivityEmailForgotPwdBinding
import tn.esprit.taktakandroid.databinding.LayoutDialogBinding
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.uis.common.otpVerification.OTPActivity
import tn.esprit.taktakandroid.utils.Resource

const val TAG = "EmailForgotPwdActivity"
class EmailForgotPwdActivity : AppCompatActivity() {
    private lateinit var mainView:ActivityEmailForgotPwdBinding
    private lateinit var viewModel: EmailForgotPwdViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainView=ActivityEmailForgotPwdBinding.inflate(layoutInflater)
        setContentView(mainView.root)

        val userRepository = UserRepository()
        val viewModelProviderFactory = EmailForgotPwdViewModelProviderFactory(userRepository,application)

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
                    progressBarVisibility(false)
                    result.data?.let {
                        Intent(this, OTPActivity::class.java).also {
                            it.putExtra("email",viewModel.email.value.toString())
                            startActivity(it)
                            finish()
                        }
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false)
                    result.message?.let { msg ->
                        showDialog(msg)
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true)
                }
            }
        })

        mainView.btnSendEmail.setOnClickListener {
            viewModel.sendOtp()
        }

    }

    private fun progressBarVisibility(visible: Boolean) {
        if (visible) {
            mainView.progressBar.visibility = View.VISIBLE
        } else {
            mainView.progressBar.visibility = View.GONE
        }
    }
    private fun showDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        val binding = LayoutDialogBinding.inflate(layoutInflater)

        builder.setView(binding.root)

        val dialog = builder.create()

        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.tvMessage.text = message

        binding.tvBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
    }

}