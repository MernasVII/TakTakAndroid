package tn.esprit.taktakandroid.models.requests

data class SendLinkRequest(
    val email: String,
    val link: String
)