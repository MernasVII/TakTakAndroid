package tn.esprit.miniprojetinterfaces.Sheets

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.SheetFragmentSettingsBinding


class SettingsSheet : BottomSheetDialogFragment() {
    private lateinit var mainView:SheetFragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView=SheetFragmentSettingsBinding.inflate(layoutInflater,container,false)
        setupLangSpinner()
        return mainView.root
    }


    fun setupLangSpinner() {
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item_lang,
            listOf("English", "French")
        )
        adapter.setDropDownViewResource(R.layout.spinner_custom_dropdown)
        mainView.spLang.adapter = adapter
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d("Debug", "Dismissed onDismiss")
    }


}