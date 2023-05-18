package tn.esprit.taktakandroid.uis.customer.myRequests

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.RequestsRepository
import tn.esprit.taktakandroid.repositories.UserRepository

class MyRequestsViewModelFactory (
    val requestsRepository: RequestsRepository,
    val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyRequestsViewModel(requestsRepository,application) as T
    }
}