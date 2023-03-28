package tn.esprit.taktakandroid.uis.common.emailForgotPwd

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.UserRepository

@Suppress("UNCHECKED_CAST")
class EmailForgotPwdViewModelProviderFactory(
    val userRepository: UserRepository,
    val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EmailForgotPwdViewModel(userRepository,application) as T
    }
}