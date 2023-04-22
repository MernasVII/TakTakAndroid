package tn.esprit.taktakandroid.uis.common.bid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.BidRepository

class BidViewModelFactory (
    val bidRepository: BidRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BidViewModel(bidRepository) as T
    }
}