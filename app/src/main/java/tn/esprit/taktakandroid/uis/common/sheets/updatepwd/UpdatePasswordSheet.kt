package tn.esprit.taktakandroid.uis.common.sheets.updatepwd

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.SheetFragmentUpdatePasswordBinding
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.uis.SheetBaseFragment
import tn.esprit.taktakandroid.utils.Resource


class UpdatePasswordSheet (user: User) : SheetBaseFragment() {
    private val TAG="UpdatePasswordSheet"

    lateinit var viewModel: UpdatePasswordViewModel
    private lateinit var mainView: SheetFragmentUpdatePasswordBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentUpdatePasswordBinding.inflate(layoutInflater, container, false)
        val userRepository = UserRepository()
        viewModel = ViewModelProvider(this, UpdatePasswordViewModelFactory(userRepository))[UpdatePasswordViewModel::class.java]

        setUpEditTexts()
        errorHandling()

        observeViewModel()
        mainView.btnSaveChanges.setOnClickListener {
            lifecycleScope.launch {
                viewModel.updatePwd()
            }
        }

        return mainView.root
    }

    private fun observeViewModel() {
        viewModel.updatePwdRes.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Resource.Success -> {
                    progressBarVisibility(false,mainView.spinkitView)
                    result.data?.let {
                        Toast.makeText(requireContext(), getString(R.string.pwd_updated), Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }
                is Resource.Error -> {
                    progressBarVisibility(false,mainView.spinkitView)
                    result.message?.let { msg ->
                        showDialog(msg)
                    }
                }
                is Resource.Loading -> {
                    progressBarVisibility(true,mainView.spinkitView)
                }
            }
        })
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
                    error = viewModel.oldPwdError.value
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
                    error = viewModel.newPwdError.value
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