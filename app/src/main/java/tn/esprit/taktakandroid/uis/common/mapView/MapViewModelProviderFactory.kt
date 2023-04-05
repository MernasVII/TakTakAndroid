package tn.esprit.taktakandroid.uis.common.mapView

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.repositories.UserRepository
@Suppress("UNCHECKED_CAST")
class MapViewModelProviderFactory(
    val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel(application) as T
    }
}