package tn.esprit.taktakandroid.uis.common.sheets.editprofile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.UserRepository

class EditProfileViewModelProviderFactory (
    val userRepository: UserRepository,
    val application: Application
    ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EditProfileViewModel(userRepository,application) as T
    }
}