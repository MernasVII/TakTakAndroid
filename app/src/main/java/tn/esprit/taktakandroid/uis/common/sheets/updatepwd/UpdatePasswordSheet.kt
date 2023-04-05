package tn.esprit.taktakandroid.uis.common.sheets.updatepwd

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.databinding.SheetFragmentUpdatePasswordBinding
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.Resource


class UpdatePasswordSheet (user: User) : BottomSheetDialogFragment() {
    private val TAG="UpdatePasswordSheet"

    lateinit var viewModel: UpdatePasswordViewModel
    private lateinit var mainView: SheetFragmentUpdatePasswordBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentUpdatePasswordBinding.inflate(layoutInflater, container, false)
        val userRepository = UserRepository()
        viewModel = ViewModelProvider(
            this,
            UpdatePasswordViewModelFactory(userRepository)
        )[UpdatePasswordViewModel::class.java]
        mainView.btnSaveChanges.setOnClickListener { dismiss() }
        setUpEditTexts()
        errorHandling()

        mainView.btnSaveChanges.setOnClickListener {
            lifecycleScope.launch {
                viewModel.updatePwd()
            }
        }

        return mainView.root
    }

    private fun setUpEditTexts() {
        mainView.etOldPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setOldPwd(s.toString())
                viewModel.removeOldPwdError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        mainView.etNewPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setNewPwd(s.toString())
                viewModel.removeNewPwdError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun errorHandling() {
        viewModel.oldPwdError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlOldPassword.apply {
                    error = viewModel.oldPwd.value
                    isErrorEnabled = true
                }
            } else {
                mainView.tlOldPassword.apply {
                    isErrorEnabled = false
                }
            }
        }
        viewModel.newPwdError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlNewPassword.apply {
                    error = viewModel.newPwd.value
                    isErrorEnabled = true
                }
            } else {
                mainView.tlNewPassword.apply {
                    isErrorEnabled = false
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d(TAG, "Dismissed onDismiss")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Dismissed onDestroy")
    }


}