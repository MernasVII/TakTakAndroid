package tn.esprit.taktakandroid.uis.sp.sheets

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.FragmentUpdateWorkDescriptionSheetBinding


class UpdateWorkDescriptionSheet : BottomSheetDialogFragment() {

    private lateinit var mainView: FragmentUpdateWorkDescriptionSheetBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainView =
            FragmentUpdateWorkDescriptionSheetBinding.inflate(layoutInflater, container, false)
        setupSheetBehaivor()
        mainView.btnSaveChanges.setOnClickListener { dismiss() }

        return mainView.root
    }

    fun setupSheetBehaivor() {
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            state = STATE_EXPANDED
            isDraggable = false
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d("Debug", "dissmised onDismiss")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Debug", "dissmised onDestroy")
    }


}