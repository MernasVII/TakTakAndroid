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
import tn.esprit.taktakandroid.uis.common.HomeActivity
import tn.esprit.taktakandroid.uis.common.otpVerification.OtpViewModel
import tn.esprit.taktakandroid.uis.common.otpVerification.OtpViewModelProviderFactory
import tn.esprit.taktakandroid.utils.Resource

const val TAG = "ResetPwdActivity"

class ResetPwdActivity : AppCompatActivity() {
    private lateinit var mainView: ActivityResetPwdBinding
    private lateinit var viewModel: ResetPwdViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainView = ActivityResetPwdBinding.inflate(layoutInflater)
        setContentView(mainView.root)

        val userRepository = UserRepository()
        val viewModelProviderFactory = ResetPwdViewModelProviderFactory(userRepository, application)

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
                    progressBarVisibility(false)
                    result.data?.let {
                        lifecycleScope.launch {
                            showSnackbar(it.message)
                            delay(1000L)
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

    private fun progressBarVisibility(visible: Boolean) {
        if (visible) {
            mainView.progressBar.visibility = View.VISIBLE
        } else {
            mainView.progressBar.visibility = View.GONE
        }
    }

    private fun showSnackbar(message: String) {
        val snackbar = Snackbar
            .make(mainView.cl, message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }
}