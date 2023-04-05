package tn.esprit.taktakandroid.uis.common.sheets.editprofile

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tn.esprit.taktakandroid.databinding.SheetFragmentEditProfileBinding
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.uis.HomeViewModel
import tn.esprit.taktakandroid.uis.common.HomeActivity


class EditProfileSheet (private val user: User) : BottomSheetDialogFragment() {
    private val TAG="EditProfileSheet"

    lateinit var viewModel: HomeViewModel
    private lateinit var mainView: SheetFragmentEditProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentEditProfileBinding.inflate(layoutInflater, container, false)
        viewModel = (activity as HomeActivity).viewModel

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