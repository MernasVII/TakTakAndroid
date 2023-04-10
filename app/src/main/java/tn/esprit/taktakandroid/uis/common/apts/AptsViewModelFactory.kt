package tn.esprit.taktakandroid.uis.common.apts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.AptRepository

class AptsViewModelFactory (
    private val aptRepository: AptRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AptsViewModel(aptRepository) as T
    }
}