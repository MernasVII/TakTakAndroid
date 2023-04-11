package tn.esprit.taktakandroid.models.entities

import java.io.Serializable

data class Notification (
    var _id: String? = null,
    val read: Boolean,
    val apt: Appointment? = null,
    val bid: Bid? = null,
    val createdAt: String,
    val action: String,
    val content: String
) : Serializable