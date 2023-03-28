package tn.esprit.taktakandroid.uis.common.login

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.databinding.ActivityLoginBinding
import tn.esprit.taktakandroid.databinding.LayoutDialogBinding
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.uis.common.emailForgotPwd.EmailForgotPwdActivity
import tn.esprit.taktakandroid.uis.common.HomeActivity

import tn.esprit.taktakandroid.utils.Resource


const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {

    private lateinit var mainView: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainView = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mainView.root)


        val userRepository = UserRepository()
        val viewModelProviderFactory = LoginViewModelProviderFactory(userRepository,application)

        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[LoginViewModel::class.java]


        mainView.etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setEmail(s.toString().trim().lowercase())
                viewModel.removeEmailError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        mainView.etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setPassword(s.toString())
                viewModel.removePwdError()
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

        viewModel.passwordError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlPassword.apply {
                    error = viewModel.passwordError.value
                    isErrorEnabled = true
                }
            } else {
                mainView.tlPassword.apply {
                    isErrorEnabled = false
                }
            }
        }

        viewModel.loginResult.observe(this@LoginActivity, Observer { result ->
            when (result) {
                is Resource.Success -> {
                    progressBarVisibility(false)
                    result.data?.let {
                        Intent(this, HomeActivity::class.java).also {
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

        mainView.btnLogin.setOnClickListener {
            viewModel.login()
        }

        mainView.tvForgotPwd.setOnClickListener{
            startActivity(Intent(this, EmailForgotPwdActivity::class.java))
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