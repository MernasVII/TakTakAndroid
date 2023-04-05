package tn.esprit.taktakandroid.models.updateworkdesc

data class UpdateWorkDescRequest(
    val speciality: String,
    val tos: ArrayList<String>,
    val workDays: ArrayList<String>
)