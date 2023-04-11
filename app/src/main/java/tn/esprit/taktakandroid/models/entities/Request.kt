package tn.esprit.taktakandroid.models.entities

import java.io.Serializable

data class Request (
    val __v: Int,
    val _id: String,
    val bids: List<Any>,
    val createdAt: String,
    val customer: User,
    val date: String,
    val desc: String,
    val isClosed: Boolean,
    val location: String,
    val noAptScheduled: Boolean,
    val tos: String,
    val updatedAt: String
) : Serializable

