package tn.esprit.taktakandroid.uis.home

import androidx.lifecycle.ViewModel
import tn.esprit.taktakandroid.repositories.UserRepository

class HomeViewModel(
    val userRepository: UserRepository
) : ViewModel() {
    private val TAG:String="HomeViewModel"
}