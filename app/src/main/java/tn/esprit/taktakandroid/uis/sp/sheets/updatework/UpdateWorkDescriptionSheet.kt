package tn.esprit.taktakandroid.uis.sp.sheets.updatework

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tn.esprit.taktakandroid.databinding.SheetFragmentUpdateWorkDescriptionBinding
import tn.esprit.taktakandroid.models.entities.User


class UpdateWorkDescriptionSheet (private val user: User) : BottomSheetDialogFragment() {
    private val TAG="UpdateWorkDescriptionSheet"

    private lateinit var mainView: SheetFragmentUpdateWorkDescriptionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainView =
            SheetFragmentUpdateWorkDescriptionBinding.inflate(layoutInflater, container, false)
        setupSheetBehaivor()

        setData()

        mainView.btnSaveChanges.setOnClickListener { dismiss() }

        return mainView.root
    }

    private fun setData() {
        mainView.etSpeciality.setText(user.speciality)
    }

    fun setupSheetBehaivor() {
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            state = STATE_EXPANDED
            isDraggable = false
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