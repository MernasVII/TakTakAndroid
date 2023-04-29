package tn.esprit.taktakandroid.uis.sp.sheets.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.UserRepository

class WalletViewModelFactory (
    val userRepository: UserRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WalletViewModel(userRepository) as T
    }
}