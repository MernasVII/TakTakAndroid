package tn.esprit.taktakandroid.uis.customer.bookapt

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.repositories.AptRepository

class BookAptViewModelFactory (val aptRepo: AptRepository, val sp: User,val application: Application ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BookAptViewModel(aptRepo,sp,application) as T
    }
}