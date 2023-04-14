package tn.esprit.taktakandroid.models.requests

data class ResetPwdRequest(val email: String?,
                           val new_pwd: String?
)