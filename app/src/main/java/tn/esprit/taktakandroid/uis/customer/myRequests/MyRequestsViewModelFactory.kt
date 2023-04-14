package tn.esprit.taktakandroid.uis.customer.myRequests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.RequestsRepository
import tn.esprit.taktakandroid.repositories.UserRepository

class MyRequestsViewModelFactory (
    val requestsRepository: RequestsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyRequestsViewModel(requestsRepository) as T
    }
}