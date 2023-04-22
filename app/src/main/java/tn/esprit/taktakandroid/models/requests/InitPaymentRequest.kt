package tn.esprit.taktakandroid.models.requests

data class InitPaymentRequest(
    val description: String,
    val email: String,
    val firstname: String,
    val lastname: String,
    val price: Float
)