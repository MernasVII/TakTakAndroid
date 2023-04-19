package tn.esprit.taktakandroid.models.responses

import tn.esprit.taktakandroid.models.entities.Bid

data class SentBidsResponse(
    val bids: List<Bid>
)