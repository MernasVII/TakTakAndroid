package tn.esprit.taktakandroid.models.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Appointment (
    var _id: String? = null,
    val createdAt: String,
    val customer: User,
    val date: String,
    val desc: String,
    val isAccepted: Boolean,
    val isArchived: Boolean,
    val location: String,
    val postpone: Int,
    val rate: Float,
    val sp: User,
    val state: Int,
    val tos: String,
    val updatedAt: String
) : Serializable, Parcelable