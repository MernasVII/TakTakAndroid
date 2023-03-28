package tn.esprit.taktakandroid.uis.common.resetPwd

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.UserRepository

@Suppress("UNCHECKED_CAST")
class ResetPwdViewModelProviderFactory(
    private val userRepository: UserRepository,
    val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ResetPwdViewModel(userRepository,application) as T
    }
}