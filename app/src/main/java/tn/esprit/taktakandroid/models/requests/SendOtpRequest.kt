package tn.esprit.taktakandroid.models.requests

data class SendOtpRequest( val email: String?,
val resetCode: String?
)