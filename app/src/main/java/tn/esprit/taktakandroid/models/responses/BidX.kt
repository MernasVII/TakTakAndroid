package tn.esprit.taktakandroid.models.responses

data class BidX(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val isAccepted: Boolean,
    val isDeclined: Boolean,
    val price: Int,
    val request: RequestXX,
    val sp: SpXX,
    val updatedAt: String
)