package tn.esprit.taktakandroid.uis.common.aptsarchived

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.AptRepository

class ArchivedAptsViewModelFactory (
    private val aptRepository: AptRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ArchivedAptsViewModel(aptRepository) as T
    }
}