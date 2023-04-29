package tn.esprit.taktakandroid.uis.sp.sheets.wallet

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.SheetFragmentWalletBinding
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.uis.SheetBaseFragment
import tn.esprit.taktakandroid.uis.sp.sheets.updatework.UpdateWorkDescriptionViewModel
import tn.esprit.taktakandroid.uis.sp.sheets.updatework.UpdateWorkDescriptionViewModelFactory
import tn.esprit.taktakandroid.utils.Resource

const val TAG="WalletSheet"
class WalletSheet : SheetBaseFragment() {

    private lateinit var mainView: SheetFragmentWalletBinding
    private lateinit var viewModel: WalletViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentWalletBinding.inflate(layoutInflater, container, false)

        return mainView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repo = UserRepository()
        viewModel = ViewModelProvider(
            this,
            WalletViewModelFactory(repo)
        )[WalletViewModel::class.java]

        mainView.etPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setPassword(s.toString())
                viewModel.removePwdError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        viewModel.pwdError.observe(this) { _errorTxt ->
            if (_errorTxt.isNotEmpty()) {
                mainView.tlPassword.apply {
                    error = viewModel.pwdError.value
                    isErrorEnabled = true
                }
            } else {
                mainView.tlPassword.apply {
                    isErrorEnabled = false
                }
            }
        }


        mainView.btnWithdraw.setOnClickListener { dismiss() }
        mainView.btnConfirm.setOnClickListener { viewModel.verifyPassword() }

        viewModel.checkPWDRes.observe(viewLifecycleOwner)
            { result ->
                when (result) {
                    is Resource.Success -> {
                       // progressBarVisibility(false,mainView.spinkitView)
                        result.data?.let {
                            Toast.makeText(requireContext(), "User authenticated!", Toast.LENGTH_SHORT).show()
                            showBalance()
                        }
                    }
                    is Resource.Error -> {
                       // progressBarVisibility(false,mainView.spinkitView)
                        result.message?.let { msg ->
                            showDialog(msg)
                        }
                    }
                    is Resource.Loading -> {
                       // progressBarVisibility(true,mainView.spinkitView)
                    }
                }

        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d("Debug", "Dismissed onDismiss")
    }

    private fun showBalance() {
        mainView.tvWalletAuthTitle.visibility = View.GONE
        mainView.tlPassword.visibility = View.GONE
        mainView.btnConfirm.visibility = View.GONE

        mainView.tvWalletTitle.visibility = View.VISIBLE
        mainView.tlBalance.visibility = View.VISIBLE
        mainView.btnWithdraw.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Debug", "Dismissed onDestroy")
    }


}