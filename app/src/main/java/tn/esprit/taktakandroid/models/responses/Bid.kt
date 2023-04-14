package tn.esprit.taktakandroid.models.responses

data class Bid(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val isAccepted: Boolean,
    val isDeclined: Boolean,
    val price: Int,
    val request: RequestX,
    val sp: SpX,
    val updatedAt: String
)