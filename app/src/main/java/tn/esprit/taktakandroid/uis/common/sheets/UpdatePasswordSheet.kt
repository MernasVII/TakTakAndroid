package tn.esprit.miniprojetinterfaces.Sheets

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tn.esprit.taktakandroid.databinding.SheetFragmentUpdatePasswordBinding
import tn.esprit.taktakandroid.models.entities.User


class UpdatePasswordSheet (user: User) : BottomSheetDialogFragment() {
    private val TAG="UpdatePasswordSheet"

    private lateinit var mainView: SheetFragmentUpdatePasswordBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentUpdatePasswordBinding.inflate(layoutInflater, container, false)
        mainView.btnSaveChanges.setOnClickListener { dismiss() }
        return mainView.root
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