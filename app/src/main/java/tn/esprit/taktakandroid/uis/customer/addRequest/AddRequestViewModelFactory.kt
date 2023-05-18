package tn.esprit.taktakandroid.uis.customer.addRequest

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.RequestsRepository

class AddRequestViewModelFactory (
    val requestsRepository: RequestsRepository,
    val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddRequestViewModel(requestsRepository,application) as T
    }
}