package tn.esprit.miniprojetinterfaces.Sheets

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.SheetFragmentSettingsBinding
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants


class SettingsSheet : BottomSheetDialogFragment() {
    private lateinit var mainView:SheetFragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView=SheetFragmentSettingsBinding.inflate(layoutInflater,container,false)
        setupLangSpinner()
        //setupToggleTheme()
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

    /*fun setupToggleTheme(){
        //get from datastore and set switch checked if dark theme is set
        var isDarkThemeSet:Boolean
        lifecycleScope.launch(Dispatchers.Main) {
            isDarkThemeSet = AppDataStore.readBool(Constants.DARK_THEME_SET)!!
            if(isDarkThemeSet){
                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                mainView.swTheme.isChecked = isDarkThemeSet
            }
        }

        mainView.swTheme.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                lifecycleScope.launch(Dispatchers.Main) {
                    AppDataStore.writeBool(Constants.DARK_THEME_SET,true)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
            } else {
                lifecycleScope.launch(Dispatchers.Main) {
                    AppDataStore.writeBool(Constants.DARK_THEME_SET,false)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        })
    }*/

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d("Debug", "Dismissed onDismiss")
    }


}