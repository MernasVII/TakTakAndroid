package tn.esprit.taktakandroid.uis.customer.sheets

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tn.esprit.taktakandroid.databinding.SheetFragmentSwitchToProBinding


class SwitchToProSheet : BottomSheetDialogFragment() {
    private lateinit var mainView: SheetFragmentSwitchToProBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainView = SheetFragmentSwitchToProBinding.inflate(layoutInflater, container, false)
        setupSheetBehaivor()
        mainView.btnSaveChanges.setOnClickListener { dismiss() }

        return mainView.root
    }

    private fun setupSheetBehaivor() {
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            isDraggable = false
        }
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