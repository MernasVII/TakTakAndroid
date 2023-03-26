package tn.esprit.miniprojetinterfaces.Sheets

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.FragmentChatSheetBinding
import tn.esprit.taktakandroid.databinding.FragmentEditProfileSheetBinding


class EditProfileSheet : BottomSheetDialogFragment() {
    private lateinit var mainView: FragmentEditProfileSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = FragmentEditProfileSheetBinding.inflate(layoutInflater, container, false)
        mainView.btnSaveChanges.setOnClickListener { dismiss() }
        return mainView.root
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