package tn.esprit.taktakandroid.uis.sp.spRequests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.RequestsRepository

class AllRequestsViewModelFactory (
    val requestsRepository: RequestsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AllRequestsViewModel(requestsRepository) as T
    }
}