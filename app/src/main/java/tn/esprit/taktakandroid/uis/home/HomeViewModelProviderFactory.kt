package tn.esprit.taktakandroid.uis.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.NotifRepository
import tn.esprit.taktakandroid.repositories.UserRepository

class HomeViewModelProviderFactory (
    val notifRepository: NotifRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(notifRepository) as T
    }
}