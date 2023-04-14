package tn.esprit.taktakandroid.models.responses

import tn.esprit.taktakandroid.models.entities.Request

data class UserArchivedReqResponse(
    val archivedRequests: List<Request>
)