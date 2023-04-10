package tn.esprit.taktakandroid.models.responses

import tn.esprit.taktakandroid.models.entities.Appointment

data class AptsResponse(
    val appointments: List<Appointment>
)