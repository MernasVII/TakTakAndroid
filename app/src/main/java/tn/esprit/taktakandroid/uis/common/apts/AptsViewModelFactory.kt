package tn.esprit.taktakandroid.uis.common.apts

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.AptRepository

class AptsViewModelFactory (
    private val aptRepository: AptRepository,
    private val app: Application,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AptsViewModel(aptRepository,app) as T
    }
}