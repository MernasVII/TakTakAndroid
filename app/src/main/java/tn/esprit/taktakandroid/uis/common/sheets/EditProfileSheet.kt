package tn.esprit.miniprojetinterfaces.Sheets

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tn.esprit.taktakandroid.databinding.SheetFragmentEditProfileBinding
import tn.esprit.taktakandroid.models.User


class EditProfileSheet (private val user: User) : BottomSheetDialogFragment() {
    private val TAG="EditProfileSheet"

    private lateinit var mainView: SheetFragmentEditProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentEditProfileBinding.inflate(layoutInflater, container, false)

        setData()

        mainView.btnSaveChanges.setOnClickListener { dismiss() }

        return mainView.root
    }

    private fun setData() {
        mainView.etFirstname.setText(user.firstname)
        mainView.etLastname.setText(user.lastname)
        mainView.etEmail.setText(user.email)
        mainView.etAddress.setText(user.address)
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