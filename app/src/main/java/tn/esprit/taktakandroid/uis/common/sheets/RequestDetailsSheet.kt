package tn.esprit.miniprojetinterfaces.Sheets

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tn.esprit.taktakandroid.databinding.SheetFragmentRequestDetailsBinding
import tn.esprit.taktakandroid.models.entities.Request


class RequestDetailsSheet (private val request: Request): BottomSheetDialogFragment() {
    private lateinit var mainView: SheetFragmentRequestDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentRequestDetailsBinding.inflate(layoutInflater, container, false)
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
        return mainView.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupDialogBehaivor()
        setData()
    }
    private fun setData() {
        mainView.etDateTime.setText(request.date)
        mainView.etTos.setText(request.tos)
        mainView.etDescription.setText(request.desc)
        mainView.etAddress.setText(request.location)
    }
    private fun setupDialogBehaivor() {
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            state = BottomSheetBehavior.STATE_EXPANDED

        }
    }


}