package tn.esprit.taktakandroid.models.responses

import tn.esprit.taktakandroid.models.entities.Bid

data class ReceivedBidsResponse(
    val receivedBids: List<Bid>
)