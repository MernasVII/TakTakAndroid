package tn.esprit.miniprojetinterfaces.Sheets

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.databinding.LayoutDialogBinding
import tn.esprit.taktakandroid.databinding.SheetFragmentSettingsBinding
import tn.esprit.taktakandroid.uis.SplashActivity
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
        setupToggleTheme()
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

    fun setupToggleTheme(){
        //get from datastore and set switch checked if dark theme is set
        var isDarkThemeSet:Boolean
        var isInitialized = false
        lifecycleScope.launch(Dispatchers.Main) {
            if(AppDataStore.readBool(Constants.DARK_THEME_SET)!=null){
                isDarkThemeSet = AppDataStore.readBool(Constants.DARK_THEME_SET)!!
                if(isDarkThemeSet){
                    //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    mainView.swTheme.isChecked = isDarkThemeSet
                }
            }
            isInitialized = true
        }

        mainView.swTheme.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isInitialized){
                showRestartDialog(isChecked)
            }
        })
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        Log.d("Debug", "Dismissed onDismiss")
    }

    private fun restartApp(){
        val intent = Intent(requireActivity().applicationContext, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        requireActivity().startActivity(intent)
        requireActivity().finishAffinity()
    }

    private fun saveThemeChoice(isChecked: Boolean) {
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                if (isChecked) {
                    AppDataStore.writeBool(Constants.DARK_THEME_SET,true)
                    //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppDataStore.writeBool(Constants.DARK_THEME_SET,false)
                    //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            } finally {
                restartApp()
            }
        }
    }

    private fun showRestartDialog(isChecked:Boolean) {
        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.Main) {
                val builder = AlertDialog.Builder(requireContext())
                val binding = LayoutDialogBinding.inflate(layoutInflater)
                builder.setView(binding.root)
                val dialog = builder.create()
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                binding.tvTitle.text="Warning"
                binding.tvBtn.text="Restart"
                binding.tvMessage.text = "You should restart the app"
                binding.tvBtn.setOnClickListener {
                    saveThemeChoice(isChecked)
                }
                dialog.show()
            }
        }
    }



}