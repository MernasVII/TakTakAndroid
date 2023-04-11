package tn.esprit.taktakandroid.models.requests

data class UpdatePwdRequest(
    val old_pwd: String,
    val new_pwd: String
    )