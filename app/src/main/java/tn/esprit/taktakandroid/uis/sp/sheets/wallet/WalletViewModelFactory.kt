package tn.esprit.taktakandroid.uis.sp.sheets.wallet

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.repositories.WalletRepository

class WalletViewModelFactory (
    val userRepository: UserRepository,
    val walletRepository: WalletRepository,
    val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WalletViewModel(userRepository,walletRepository,application) as T
    }
}