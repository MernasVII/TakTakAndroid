package tn.esprit.taktakandroid.uis.common.sheets.updatepwd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.UserRepository

class UpdatePasswordViewModelFactory (
    val userRepository: UserRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UpdatePasswordViewModel(userRepository) as T
    }
}