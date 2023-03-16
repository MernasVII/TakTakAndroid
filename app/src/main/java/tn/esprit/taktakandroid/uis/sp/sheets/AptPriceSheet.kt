package tn.esprit.taktakandroid.uis.sp.sheets

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shawnlin.numberpicker.NumberPicker
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.FragmentAptPriceSheetBinding


class AptPriceSheet : BottomSheetDialogFragment() {
    private lateinit var mainView: FragmentAptPriceSheetBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = FragmentAptPriceSheetBinding.inflate(layoutInflater, container, false)

        mainView.btnProceed.setOnClickListener {
            dismiss()
        }

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


