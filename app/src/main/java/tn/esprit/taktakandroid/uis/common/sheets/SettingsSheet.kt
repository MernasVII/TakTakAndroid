package tn.esprit.miniprojetinterfaces.Sheets

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.SheetFragmentSettingsBinding
import tn.esprit.taktakandroid.uis.home.HomeActivity
import tn.esprit.taktakandroid.utils.AppDataStore
import java.util.Locale


class SettingsSheet : BottomSheetDialogFragment() {
    private lateinit var mainView: SheetFragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentSettingsBinding.inflate(layoutInflater, container, false)
        setupLangSpinner()
        return mainView.root
    }

    fun setupLangSpinner() {

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            R.layout.spinner_item_lang,
            listOf("English", "Français")
        )

        adapter.setDropDownViewResource(R.layout.spinner_custom_dropdown)
        mainView.spLang.adapter = adapter
        lifecycleScope.launch(Dispatchers.IO) {
            val langStored = AppDataStore.readString("LANG")
            if (!langStored.isNullOrEmpty()) {
                if(langStored == "en"){
                    mainView.spLang.setSelection(0, false)
                }
                else{
                    mainView.spLang.setSelection(1, false)
                }
            } else {
                mainView.spLang.setSelection(0, false)
            }


            mainView.spLang.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedLang = parent?.getItemAtPosition(position).toString()

                    if (selectedLang == "English") {
                        lifecycleScope.launch(Dispatchers.IO) {
                            AppDataStore.writeString(
                                "LANG",
                                "en"
                            )
                        }
                        setLocal(requireActivity(), "en")
                        requireActivity().finish()
                        requireActivity().startActivity(
                            Intent(
                                requireContext(),
                                HomeActivity::class.java
                            )
                        )
                    } else {
                        lifecycleScope.launch(Dispatchers.IO) {
                            AppDataStore.writeString(
                                "LANG",
                                "fr"
                            )
                        }
                        setLocal(requireActivity(), "fr")
                        requireActivity().finish()
                        requireActivity().startActivity(
                            Intent(
                                requireContext(),
                                HomeActivity::class.java
                            )
                        )
                    }


                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }


    }


    private fun setLocal(activity: Activity, langCode: String) {
        val customLocale = Locale(langCode)
        Locale.setDefault(customLocale)
        val resources = activity.resources
        val config = resources.configuration
        config.setLocale(customLocale)
        resources.updateConfiguration(config, resources.displayMetrics)

    }

}