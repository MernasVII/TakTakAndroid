package tn.esprit.taktakandroid.uis.sp.sheets

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shawnlin.numberpicker.NumberPicker

import tn.esprit.taktakandroid.R


class PostponeAptSheet : BottomSheetDialogFragment() {
    private lateinit var numberPicker: NumberPicker
    private val data = arrayOf("5", "10", "15", "20", "25", "30")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_postpone_apt_sheet, container, false)
        setupPicker(view)
        view.findViewById<Button>(R.id.btnPostpone).setOnClickListener {
            Log.v("PostponeAptSheet", data[numberPicker.value - 1])
            dismiss()
        }

        return view
    }


    fun setupPicker(v: View) {
        numberPicker = v.findViewById(R.id.npMinutes)

        numberPicker.minValue = 1
        numberPicker.maxValue = data.size
        numberPicker.displayedValues = data
        numberPicker.value = 1
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


