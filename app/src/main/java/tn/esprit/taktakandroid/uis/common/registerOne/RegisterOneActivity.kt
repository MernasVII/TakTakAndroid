package tn.esprit.taktakandroid.uis.common.registerOne

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.databinding.ActivityRegisterOneBinding
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.uis.common.BaseActivity
import tn.esprit.taktakandroid.uis.common.registerTwo.RegisterTwoActivity
import tn.esprit.taktakandroid.utils.Resource
const val TAG = "RegisterOneActivity"

class RegisterOneActivity : BaseActivity() {
    private lateinit var mainView: ActivityRegisterOneBinding
    private lateinit var viewModel: RegisterOneViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainView = ActivityRegisterOneBinding.inflate(layoutInflater)
        setContentView(mainView.root)

        val userRepository = UserRepository()
        val viewModelProviderFactory = RegisterOneViewModelProviderFactory(userRepository)

        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[RegisterOneViewModel::class.java]

        setUpEditTexts()
        errorHandling()


        viewModel.signUpResult.observe(this@RegisterOneActivity) { result ->
            when (result) {
                is Resource.Success -> {
                    progressBarVisibility(false, mainView.progressBar)
                    result.data?.let {
                        lifecycleScope.launch {
                            showSnackbar(it.message, mainView.cl)
                            delay(1000L)
                            finish()
                        }
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false, mainView.progressBar)
                    result.message?.let { msg ->
                        showDialog(msg)
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true, mainView.progressBar)
                }
            }
        }

        mainView.btnRegCust.setOnClickListener {
            viewModel.signUp()
        }
        mainView.etAddress.setOnClickListener {
            mainView.etAddress.setText("address")
            viewModel.setAddress("address")

        }
        mainView.btnRegSp.setOnClickListener {
            if (viewModel.fieldsValidation(
                    viewModel.firstname.value,
                    viewModel.lastname.value,
                    viewModel.password.value,
                    viewModel.address.value,
                    viewModel.email.value,
                )
            ) Intent(this, RegisterTwoActivity::class.java).also {
                it.putExtra("firstname",viewModel.firstname.value)
                it.putExtra("lastname",viewModel.lastname.value)
                it.putExtra("password",viewModel.password.value)
                it.putExtra("address",viewModel.address.value)
                it.putExtra("email",viewModel.email.value)
                finishOneActivityCallback.launch(it)

            }

        }
    }


    private fun setUpEditTexts() {
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

        mainView.etFirstname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setFirstname(s.toString())
                viewModel.removeFirstnameError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        mainView.etLastname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setLastname(s.toString())
                viewModel.removeLastnameError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        mainView.etAddress.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setAddress(s.toString())
                viewModel.removeAddressError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun errorHandling() {
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
        viewModel.firstnameError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlFirstname.apply {
                    error = viewModel.firstnameError.value
                    isErrorEnabled = true
                }
            } else {
                mainView.tlFirstname.apply {
                    isErrorEnabled = false
                }
            }
        }
        viewModel.lastnameError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlLastname.apply {
                    error = viewModel.lastnameError.value
                    isErrorEnabled = true
                }
            } else {
                mainView.tlLastname.apply {
                    isErrorEnabled = false
                }
            }
        }
        viewModel.addressError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlAddress.apply {
                    error = viewModel.addressError.value
                    isErrorEnabled = true
                }
            } else {
                mainView.tlAddress.apply {
                    isErrorEnabled = false
                }
            }
        }

    }


}