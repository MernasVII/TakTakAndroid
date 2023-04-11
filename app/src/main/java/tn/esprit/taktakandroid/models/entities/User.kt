package tn.esprit.taktakandroid.models.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class User (
    val _id: String? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val address: String? = null,
    val cin: String? = null,
    val email: String? = null,
    val pic: String? = null,
    val hash: String? = null,
    val speciality: String? = null,
    val isVerified: Boolean? = null,
    val workDays: ArrayList<String>? = null,
    val tos: ArrayList<String>? = null,
    val rate: Float? = null,
) : Serializable,Parcelable