package tn.esprit.taktakandroid.uis.customer.archivedRequests

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.RequestsRepository

class MyArchivedRequestsViewModelFactory (
    val requestsRepository: RequestsRepository,
    val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MyArchivedRequestsViewModel(requestsRepository,application) as T
    }
}