package tn.esprit.taktakandroid.models.responses

data class Customer(
    val __v: Int,
    val _id: String,
    val address: String,
    val appointmentsRec: List<Any>,
    val appointmentsReq: List<String>,
    val bids: List<Any>,
    val cin: String,
    val createdAt: String,
    val email: String,
    val firstname: String,
    val hash: String,
    val isVerified: Boolean,
    val lastname: String,
    val pic: String,
    val rate: Int,
    val requests: List<String>,
    val speciality: String,
    val tos: List<Any>,
    val updatedAt: String,
    val workDays: List<Any>
)