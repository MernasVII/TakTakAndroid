package tn.esprit.taktakandroid.uis.sp.sheets.updatework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.UserRepository

class UpdateWorkDescriptionViewModelFactory (
    val userRepository: UserRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UpdateWorkDescriptionViewModel(userRepository) as T
    }
}