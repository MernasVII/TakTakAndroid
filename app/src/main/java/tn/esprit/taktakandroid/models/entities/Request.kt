package tn.esprit.taktakandroid.models.entities

import java.io.Serializable

data class Request (
    var _id: String? = null,
    val date: String,
    val location: String,
    val tos: String,
    val description: String,
    val isClosed: Boolean,
    val customer: User,
    val bids: List<Bid>,
) : Serializable