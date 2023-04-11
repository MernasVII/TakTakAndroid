package tn.esprit.taktakandroid.uis.common.notifs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.NotifRepository

class NotifsViewModelFactory (
    val notifRepository: NotifRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotifsViewModel(notifRepository) as T
    }
}