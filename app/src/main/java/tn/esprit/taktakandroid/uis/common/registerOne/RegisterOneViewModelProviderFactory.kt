package tn.esprit.taktakandroid.uis.common.registerOne

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.UserRepository
@Suppress("UNCHECKED_CAST")
class RegisterOneViewModelProviderFactory(
    val userRepository: UserRepository,
    val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RegisterOneViewModel(userRepository,application) as T
    }
}