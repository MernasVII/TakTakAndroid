package tn.esprit.taktakandroid.models.requests

import java.util.*

data class BookAptRequest(
    val date: String,
    val desc: String,
    val location: String,
    val sp: String,
    val tos: String
)