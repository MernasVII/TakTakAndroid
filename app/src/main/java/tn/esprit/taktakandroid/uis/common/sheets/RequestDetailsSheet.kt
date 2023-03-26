package tn.esprit.miniprojetinterfaces.Sheets

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.FragmentRequestDetailsBinding

class RequestDetailsSheet : BottomSheetDialogFragment() {
    private lateinit var mainView: FragmentRequestDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupDialogBehaivor()
        mainView = FragmentRequestDetailsBinding.inflate(layoutInflater, container, false)
        return mainView.root
    }

    private fun setupDialogBehaivor() {
        (dialog as? BottomSheetDialog)?.behavior?.apply {
            state = BottomSheetBehavior.STATE_EXPANDED
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