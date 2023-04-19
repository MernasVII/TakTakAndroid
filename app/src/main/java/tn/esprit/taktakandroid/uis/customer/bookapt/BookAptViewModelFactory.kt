package tn.esprit.taktakandroid.uis.customer.bookapt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.repositories.AptRepository

class BookAptViewModelFactory (val aptRepo: AptRepository, val sp: User, ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BookAptViewModel(aptRepo,sp) as T
    }
}