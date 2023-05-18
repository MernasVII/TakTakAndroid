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
import render.animations.Attention
import render.animations.Render
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.SheetFragmentWalletBinding
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.repositories.WalletRepository
import tn.esprit.taktakandroid.uis.SheetBaseFragment
import tn.esprit.taktakandroid.uis.sp.sheets.updatework.UpdateWorkDescriptionViewModel
import tn.esprit.taktakandroid.uis.sp.sheets.updatework.UpdateWorkDescriptionViewModelFactory
import tn.esprit.taktakandroid.utils.Resource

const val TAG = "WalletSheet"

class WalletSheet : SheetBaseFragment() {

    private lateinit var mainView: SheetFragmentWalletBinding
    private lateinit var viewModel: WalletViewModel
    private lateinit var render: Render

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentWalletBinding.inflate(layoutInflater, container, false)

        return mainView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        render = Render(requireContext())
        val userRepo = UserRepository()
        val walletRepo = WalletRepository()
        viewModel = ViewModelProvider(
            this,
            WalletViewModelFactory(userRepo, walletRepo,requireActivity().application)
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
                    render.setAnimation(Attention.Shake(mainView.tlPassword))
                    render.start()
                }
            } else {
                mainView.tlPassword.apply {
                    isErrorEnabled = false
                }
            }
        }


        mainView.btnWithdraw.setOnClickListener {
            val currentBalance = mainView.etBalance.text.toString().toFloat()

            if (currentBalance == 0.0f) {
                showDialog("Cannot withdraw due to insufficient funds!")
            } else {
                viewModel.withdrawMoney()
            }
       }
        mainView.btnConfirm.setOnClickListener { viewModel.verifyPassword() }

        viewModel.checkPWDResult.observe(viewLifecycleOwner)
        { result ->
            when (result) {
                is Resource.Success -> {
                     progressBarVisibility(false,mainView.spinkitView)
                    result.data?.let {
                        viewModel.getCurrentBalance()
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

        }

        viewModel.getMyBalanceResult.observe(viewLifecycleOwner)
        { result ->
            when (result) {
                is Resource.Success -> {
                     progressBarVisibility(false,mainView.spinkitView)
                    result.data?.let { resp ->
                        mainView.etBalance.setText(resp.amount.toString())
                        showBalance()
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

        }

        viewModel.withdrawMoneyResult.observe(viewLifecycleOwner)
        { result ->
            when (result) {
                is Resource.Success -> {
                    progressBarVisibility(false,mainView.spinkitView)
                    result.data?.let {
                        mainView.etBalance.setText("0.0")
                        Toast.makeText(requireContext(), "Successful withdrawal. Check email for details.", Toast.LENGTH_SHORT).show()
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