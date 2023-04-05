package tn.esprit.taktakandroid.models.updatepwd

data class UpdatePwdRequest(
    val old_pwd: String,
    val new_pwd: String
    )