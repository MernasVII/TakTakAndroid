package tn.esprit.taktakandroid.models.responses

data class ReceivedBid(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val isAccepted: Boolean,
    val isDeclined: Boolean,
    val price: Int,
    val request: Request,
    val sp: Sp,
    val updatedAt: String
)