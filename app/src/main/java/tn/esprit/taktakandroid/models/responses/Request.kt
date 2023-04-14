package tn.esprit.taktakandroid.models.responses

data class Request(
    val __v: Int,
    val _id: String,
    val bids: List<String>,
    val createdAt: String,
    val customer: String,
    val date: String,
    val desc: String,
    val isClosed: Boolean,
    val location: String,
    val noAptScheduled: Boolean,
    val tos: String,
    val updatedAt: String
)