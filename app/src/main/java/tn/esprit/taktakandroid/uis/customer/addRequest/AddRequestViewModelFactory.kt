package tn.esprit.taktakandroid.uis.customer.addRequest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.RequestsRepository

class AddRequestViewModelFactory (
    val requestsRepository: RequestsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddRequestViewModel(requestsRepository) as T
    }
}