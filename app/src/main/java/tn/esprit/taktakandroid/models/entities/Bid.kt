package tn.esprit.taktakandroid.models.entities

import java.io.Serializable

data class Bid (
    var _id: String? = null,
    val isAccepted: Boolean,
    val isDeclined: Boolean,
    val sp: User,
    val price: Float,
    val request: Request
) : Serializable