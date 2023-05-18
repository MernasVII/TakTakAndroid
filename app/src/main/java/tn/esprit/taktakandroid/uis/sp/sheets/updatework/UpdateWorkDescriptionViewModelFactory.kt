package tn.esprit.taktakandroid.uis.sp.sheets.updatework

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.UserRepository

class UpdateWorkDescriptionViewModelFactory (
    val userRepository: UserRepository,
    val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UpdateWorkDescriptionViewModel(userRepository,application) as T
    }
}