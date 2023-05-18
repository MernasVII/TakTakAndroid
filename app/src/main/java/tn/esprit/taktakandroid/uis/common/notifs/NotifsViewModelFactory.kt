package tn.esprit.taktakandroid.uis.common.notifs

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.NotifRepository

class NotifsViewModelFactory (
    val notifRepository: NotifRepository,
    val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NotifsViewModel(notifRepository,app) as T
    }
}