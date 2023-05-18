package tn.esprit.taktakandroid.uis.sp.spRequests

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.RequestsRepository

class AllRequestsViewModelFactory (
    val requestsRepository: RequestsRepository,
    val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AllRequestsViewModel(requestsRepository,application) as T
    }
}