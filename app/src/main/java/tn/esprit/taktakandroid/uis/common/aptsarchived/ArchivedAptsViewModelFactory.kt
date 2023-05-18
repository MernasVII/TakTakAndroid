package tn.esprit.taktakandroid.uis.common.aptsarchived

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.AptRepository

class ArchivedAptsViewModelFactory (
    private val aptRepository: AptRepository,
    private val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ArchivedAptsViewModel(aptRepository,app) as T
    }
}