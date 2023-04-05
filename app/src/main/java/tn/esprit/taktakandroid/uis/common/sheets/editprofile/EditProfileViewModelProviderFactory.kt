package tn.esprit.taktakandroid.uis.common.sheets.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.UserRepository

class EditProfileViewModelProviderFactory (
    val userRepository: UserRepository
    ) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EditProfileViewModel(userRepository) as T
    }
}