package tn.esprit.taktakandroid.uis.customer.bids

interface BidCustomerItemTouchHelperListener {
    fun onBidPendingSwipedLeft(bidId: String,spID:String)
    fun onBidPendingSwipedRight(bidId: String,spID:String)
}
