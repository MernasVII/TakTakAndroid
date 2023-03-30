package tn.esprit.taktakandroid.uis.common.otpVerification

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.UserRepository

@Suppress("UNCHECKED_CAST")
class OtpViewModelProviderFactory(
    private val userRepository: UserRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OtpViewModel(userRepository) as T
    }
}