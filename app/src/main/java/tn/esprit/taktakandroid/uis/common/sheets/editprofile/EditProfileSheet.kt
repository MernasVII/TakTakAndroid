package tn.esprit.taktakandroid.uis.common.sheets.editprofile

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.databinding.LayoutDialogBinding
import tn.esprit.taktakandroid.databinding.SheetFragmentEditProfileBinding
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.Resource


class EditProfileSheet(private val user: User) : BottomSheetDialogFragment() {
    private val TAG = "EditProfileSheet"

    lateinit var viewModel: EditProfileViewModel
    private lateinit var mainView: SheetFragmentEditProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentEditProfileBinding.inflate(layoutInflater, container, false)
        val userRepository = UserRepository()
        viewModel = ViewModelProvider(
            this,
            EditProfileViewModelProviderFactory(userRepository)
        )[EditProfileViewModel::class.java]
        setData()
        setUpEditTexts()
        errorHandling()
        mainView.btnSaveChanges.setOnClickListener {
            lifecycleScope.launch {
                viewModel.updateProfile()
            }
        }

        return mainView.root
    }

    private fun setData() {
        mainView.etFirstname.setText(user.firstname)
        mainView.etLastname.setText(user.lastname)
        mainView.etEmail.setText(user.email)
        mainView.etAddress.setText(user.address)
    }

    private fun setUpEditTexts() {
        val currentFirstname = mainView.etFirstname.text?.toString()?.trim()
        if (currentFirstname != null) {
            viewModel.setFirstname(currentFirstname)
        }
        mainView.etFirstname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setFirstname(s.toString().trim())
                viewModel.removeFirstnameError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val currentLastname = mainView.etLastname.text?.toString()?.trim()
        if (currentLastname != null) {
            viewModel.setLastname(currentLastname)
        }
        mainView.etLastname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setLastname(s.toString())
                viewModel.removeLastnameError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        val currentAddress = mainView.etAddress.text?.toString()?.trim()
        if (currentAddress != null) {
            viewModel.setAddress(currentAddress)
        }
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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d(TAG, "Dismissed onDismiss")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Dismissed onDestroy")
    }


}