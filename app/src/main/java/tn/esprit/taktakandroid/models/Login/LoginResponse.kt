package tn.esprit.taktakandroid.models.Login

data class LoginResponse(
    val cin: String,
    val id: String,
    val message: String,
    val token: String
)