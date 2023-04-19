package tn.esprit.taktakandroid.models.responses

import tn.esprit.taktakandroid.models.entities.Request
import tn.esprit.taktakandroid.models.entities.User

data class ReceivedBid(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val isAccepted: Boolean,
    val isDeclined: Boolean,
    val price: Int,
    val request: Request,
    val sp: User,
    val updatedAt: String
)