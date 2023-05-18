package tn.esprit.taktakandroid.uis.common.bid

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.BidRepository

class BidViewModelFactory (
    val bidRepository: BidRepository,
    val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BidViewModel(bidRepository,app) as T
    }
}