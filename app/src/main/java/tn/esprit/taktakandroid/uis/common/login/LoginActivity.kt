package tn.esprit.taktakandroid.uis.common.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import tn.esprit.taktakandroid.databinding.ActivityLoginBinding
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.uis.BaseActivity
import tn.esprit.taktakandroid.uis.common.emailForgotPwd.EmailForgotPwdActivity
import tn.esprit.taktakandroid.uis.home.HomeActivity
import tn.esprit.taktakandroid.uis.common.registerOne.RegisterOneActivity
import tn.esprit.taktakandroid.uis.common.sheets.TermsAndConditionsSheet

import tn.esprit.taktakandroid.utils.Resource
import tn.esprit.taktakandroid.utils.SocketService


const val TAG = "LoginActivity"

class LoginActivity : BaseActivity() {

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


        setUpEditTexts()

        errorHandling()

        viewModel.loginResult.observe(this@LoginActivity, Observer { result ->
            when (result) {
                is Resource.Success -> {
                    progressBarVisibility(false,mainView.progressBar)
                    result.data?.let {
                        Intent(this, HomeActivity::class.java).also {
                            startActivity(it)
                            finish()
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

        mainView.btnLogin.setOnClickListener {
            viewModel.login()
        }

        mainView.tvForgotPwd.setOnClickListener{
            startActivity(Intent(this, EmailForgotPwdActivity::class.java))
        }

        mainView.btnCreateAccount.setOnClickListener{
            startActivity(Intent(this, RegisterOneActivity::class.java))
        }

        mainView.btnGoogleLogin.setOnClickListener {
            startActivityResult.launch(viewModel.googleSignIn())
        }
        mainView.tvTermsConditions.setOnClickListener {
           displaySheet(TermsAndConditionsSheet())
        }


    }

    private fun setUpEditTexts(){
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

    }

    private fun errorHandling(){
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

    }

    private var startActivityResult=registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){ result ->
        if (result.resultCode == Activity.RESULT_OK) {

            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            viewModel.handleGoogleSignInResult(task)
        }
    }


}