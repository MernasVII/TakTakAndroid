package tn.esprit.taktakandroid.uis.sp.sheets

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tn.esprit.taktakandroid.databinding.SheetFragmentPostponeAptBinding


class PostponeAptSheet : BottomSheetDialogFragment() {

    private lateinit var mainView: SheetFragmentPostponeAptBinding
    private val data = arrayOf("5", "10", "15", "20", "25", "30")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentPostponeAptBinding.inflate(layoutInflater, container, false)
        setupPicker()
        mainView.btnPostpone.setOnClickListener {
            Log.v("PostponeAptSheet", data[mainView.npMinutes.value - 1])
            dismiss()
        }

        return mainView.root
    }


    fun setupPicker() {

        mainView.npMinutes.apply {
            minValue = 1
            maxValue = data.size
            displayedValues = data
            value = 1
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


