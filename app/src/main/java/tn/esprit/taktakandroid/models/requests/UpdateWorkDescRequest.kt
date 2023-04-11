package tn.esprit.taktakandroid.models.requests

data class UpdateWorkDescRequest(
    val speciality: String,
    val tos: ArrayList<String>,
    val workDays: ArrayList<String>
)