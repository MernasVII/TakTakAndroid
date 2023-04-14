package tn.esprit.taktakandroid.models.responses

data class SpXX(
    val __v: Int,
    val _id: String,
    val address: String,
    val appointmentsRec: List<String>,
    val appointmentsReq: List<Any>,
    val bids: List<String>,
    val cin: String,
    val createdAt: String,
    val email: String,
    val firstname: String,
    val hash: String,
    val isVerified: Boolean,
    val lastname: String,
    val pic: String,
    val rate: Int,
    val requests: List<Any>,
    val speciality: String,
    val tos: List<String>,
    val updatedAt: String,
    val workDays: List<String>
)