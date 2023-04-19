package tn.esprit.taktakandroid.models.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Request (
    val __v: Int,
    val _id: String,
    val bids: List<Bid>,
    val createdAt: String,
    val customer: User,
    val date: String,
    val desc: String,
    val isClosed: Boolean,
    val location: String,
    val noAptScheduled: Boolean,
    val tos: String,
    val updatedAt: String
) : Serializable,Parcelable

