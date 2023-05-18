package tn.esprit.taktakandroid.uis.common.payment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import tn.esprit.taktakandroid.models.entities.Appointment
import tn.esprit.taktakandroid.repositories.PaymentRepository

class PaymentViewModelFactory(
    val paymentRepository: PaymentRepository,
    val appointment: Appointment?,
    val app: Application,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PaymentViewModel(paymentRepository,appointment,app) as T
    }
}