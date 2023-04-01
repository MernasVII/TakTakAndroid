package tn.esprit.taktakandroid.models

class SignUpRequest(
    val firstname: String,
    val lastname: String,
    val hash: String? ="",
    val address: String? ="",
    val email: String?,
    val cin: String? ="",
    val speciality: String? ="",
    val tos: ArrayList<String>? = arrayListOf(),
    val workDays: ArrayList<String>? =arrayListOf(),
)