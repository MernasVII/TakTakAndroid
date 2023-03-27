package tn.esprit.taktakandroid.uis.common.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.datastore.dataStore
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.ActivityLoginBinding
import tn.esprit.taktakandroid.repositories.LoginRepository
import tn.esprit.taktakandroid.uis.common.HomeActivity
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants.AUTH_TOKEN
import tn.esprit.taktakandroid.utils.Resource

const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {

    private lateinit var mainView: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var appDataStore: AppDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mainView = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mainView.root)
        appDataStore = AppDataStore(this)
        val loginRepository = LoginRepository()
        val viewModelProviderFactory = LoginViewModelProviderFactory(loginRepository)
        viewModel =
            ViewModelProvider(this, viewModelProviderFactory).get(LoginViewModel::class.java)

        mainView.btnLogin.setOnClickListener {
            Login()
        }


        mainView.etEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setEmail(s.toString().trim())
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
            }
            else{
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
            }
            else{
                mainView.tlPassword.apply {
                    isErrorEnabled = false
                }
            }
        }


    }

    private fun Login() {
        viewModel.login()
        viewModel.loginResult.observe(this@LoginActivity, Observer { result ->
            when (result) {
                is Resource.Success -> {
                    //   hideProgressBar()
                    result.data?.let { loginResponse ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            appDataStore.writeString(AUTH_TOKEN, loginResponse.token)
                        }
                        Intent(this, HomeActivity::class.java).also {
                            startActivity(it)
                            finish()
                        }
                    }
                }
                is Resource.Error -> {
                    //    hideProgressBar()
                    result.message?.let { message ->
                        Log.e(TAG, "An error occured: $message")
                    }
                }
                is Resource.Loading -> {
                    //  showProgressBar()
                }
            }
        })


    }
}