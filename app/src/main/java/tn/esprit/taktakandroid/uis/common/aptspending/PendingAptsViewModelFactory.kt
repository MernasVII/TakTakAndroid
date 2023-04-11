package tn.esprit.taktakandroid.uis.common.aptspending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.AptRepository

class PendingAptsViewModelFactory (
    private val aptRepository: AptRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PendingAptsViewModel(aptRepository) as T
    }
}