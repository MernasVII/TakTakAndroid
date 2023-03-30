package tn.esprit.taktakandroid.uis.common.registerTwo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.UserRepository
@Suppress("UNCHECKED_CAST")
class RegisterTwoViewModelProviderFactory(
    val userRepository: UserRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RegisterTwoViewModel(userRepository) as T
    }
}