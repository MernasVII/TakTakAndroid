package tn.esprit.taktakandroid.uis.common.sheets

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
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
import tn.esprit.taktakandroid.uis.home.HomeActivity
import java.util.Locale


class SettingsSheet : BottomSheetDialogFragment() {
    private lateinit var mainView: SheetFragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainView = SheetFragmentSettingsBinding.inflate(layoutInflater, container, false)
        setupLangSpinner()
        setupToggleTheme()
        return mainView.root
    }

    private fun setupLangSpinner() {
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
                binding.tvTitle.text=requireContext().getString(R.string.warning)
                binding.tvBtn.text=requireContext().getString(R.string.restart)
                binding.tvMessage.text = requireContext().getString(R.string.should_restart)
                binding.tvBtn.setOnClickListener {
                    saveThemeChoice(isChecked)
                }
                dialog.show()
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