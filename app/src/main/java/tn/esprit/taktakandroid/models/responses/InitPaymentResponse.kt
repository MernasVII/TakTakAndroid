package tn.esprit.taktakandroid.models.responses

data class InitPaymentResponse(
    val payUrl: String,
    val paymentRef: String
)