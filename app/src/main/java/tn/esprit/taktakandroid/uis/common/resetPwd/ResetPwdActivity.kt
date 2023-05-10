package tn.esprit.taktakandroid.uis.common.resetPwd

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import render.animations.Attention
import render.animations.Render
import tn.esprit.taktakandroid.databinding.ActivityResetPwdBinding
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.uis.BaseActivity
import tn.esprit.taktakandroid.utils.Resource

const val TAG = "ResetPwdActivity"

class ResetPwdActivity : BaseActivity() {
    private lateinit var mainView: ActivityResetPwdBinding
    private lateinit var viewModel: ResetPwdViewModel
    private lateinit var render: Render

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainView = ActivityResetPwdBinding.inflate(layoutInflater)
        setContentView(mainView.root)
        render=Render(this)

        val userRepository = UserRepository()
        val viewModelProviderFactory = ResetPwdViewModelProviderFactory(userRepository)

        viewModel =
            ViewModelProvider(this, viewModelProviderFactory)[ResetPwdViewModel::class.java]

        intent.getStringExtra("email")?.let {
            viewModel.setEmail(it)
        }

        viewModel.passwordError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlNewPassword.apply {
                    error = viewModel.passwordError.value
                    isErrorEnabled = true
                    render.setAnimation(Attention.Shake(mainView.tlNewPassword))
                    render.start()
                }
            } else {
                mainView.tlNewPassword.apply {
                    isErrorEnabled = false
                }
            }
        }

        viewModel.resetPwdResult.observe(this@ResetPwdActivity) { result ->
            when (result) {
                is Resource.Success -> {
                    progressBarVisibility(false,mainView.progressBar)
                    result.data?.let {
                        lifecycleScope.launch {
                            showSnackbar(it.message,mainView.cl)
                            delay(1000L)
                            setResult(RESULT_OK)
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
        }

        mainView.etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setPassword(s.toString())
                viewModel.removePwdError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        mainView.btnConfirm.setOnClickListener {
            viewModel.resetPwd()
        }
    }



}