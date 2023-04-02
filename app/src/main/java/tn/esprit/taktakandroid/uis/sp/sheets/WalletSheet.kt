package tn.esprit.taktakandroid.uis.sp.sheets

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tn.esprit.taktakandroid.databinding.SheetFragmentWalletBinding


class WalletSheet : BottomSheetDialogFragment() {

private lateinit var mainView:SheetFragmentWalletBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView =SheetFragmentWalletBinding.inflate(layoutInflater, container, false)
        mainView.btnWithdraw.setOnClickListener { dismiss() }
        return mainView.root
    }
    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d("Debug", "Dismissed onDismiss")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Debug", "Dismissed onDestroy")
    }



}