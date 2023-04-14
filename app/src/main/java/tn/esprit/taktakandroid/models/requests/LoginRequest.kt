package tn.esprit.taktakandroid.models.requests

data class LoginRequest(
    val email: String?,
    val hash: String?
)