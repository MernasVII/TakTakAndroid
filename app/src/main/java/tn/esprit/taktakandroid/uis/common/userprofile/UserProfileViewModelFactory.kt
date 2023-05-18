package tn.esprit.taktakandroid.uis.common.userprofile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.UserRepository

class UserProfileViewModelFactory (
    val userRepo: UserRepository,
    val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserProfileViewModel(userRepo,application) as T
    }
}