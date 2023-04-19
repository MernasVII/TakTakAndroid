package tn.esprit.taktakandroid.models.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Appointment (
    val _id: String? = null,
    val createdAt: String,
    val customer: User,
    val date: String,
    val desc: String,
    val isAccepted: Boolean,
    val isArchived: Boolean,
    val location: String,
    val postpone: Int,
    var rate: Float,
    var price: Float?=null,
    val sp: User,
    var state: Int,
    val tos: String,
    val updatedAt: String
) : Serializable, Parcelable