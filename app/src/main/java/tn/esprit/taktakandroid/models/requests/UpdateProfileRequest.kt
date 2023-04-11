package tn.esprit.taktakandroid.models.requests

data class UpdateProfileRequest(
    val firstname: String,
    val lastname: String,
    val address: String
    )