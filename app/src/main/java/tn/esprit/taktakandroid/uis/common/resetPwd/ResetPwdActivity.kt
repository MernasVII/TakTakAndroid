package tn.esprit.taktakandroid.uis.common.resetPwd

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.ActivityResetPwdBinding
import tn.esprit.taktakandroid.databinding.LayoutDialogBinding
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.uis.common.BaseActivity
import tn.esprit.taktakandroid.uis.common.HomeActivity
import tn.esprit.taktakandroid.uis.common.otpVerification.OtpViewModel
import tn.esprit.taktakandroid.uis.common.otpVerification.OtpViewModelProviderFactory
import tn.esprit.taktakandroid.utils.Resource

const val TAG = "ResetPwdActivity"

class ResetPwdActivity : BaseActivity() {
    private lateinit var mainView: ActivityResetPwdBinding
    private lateinit var viewModel: ResetPwdViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainView = ActivityResetPwdBinding.inflate(layoutInflater)
        setContentView(mainView.root)

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