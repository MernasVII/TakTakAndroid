package tn.esprit.taktakandroid.uis.common.registerTwo

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.UserRepository
@Suppress("UNCHECKED_CAST")
class RegisterTwoViewModelProviderFactory(
    val userRepository: UserRepository,
    val app: Application,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RegisterTwoViewModel(userRepository,app) as T
    }
}