package tn.esprit.taktakandroid.uis.customer.spslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.UserRepository

class SPsViewModelFactory (
    val userRepo: UserRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SPsViewModel(userRepo) as T
    }
}