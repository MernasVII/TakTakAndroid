package tn.esprit.taktakandroid.uis.customer.archivedRequests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.RequestsRepository

class MyArchivedRequestsViewModelFactory (
    val requestsRepository: RequestsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyArchivedRequestsViewModel(requestsRepository) as T
    }
}