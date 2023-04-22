package tn.esprit.taktakandroid.repositories

import tn.esprit.taktakandroid.api.RetrofitInstance
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.models.requests.MakeBidRequest

class BidRepository {
    /**************** AS A CUSTOMER ****************/
    //GET RECEIVED BIDS
    suspend fun getReceivedBids(token:String,idBodyRequest: IdBodyRequest)=
        RetrofitInstance.bidApi.getReceivedBids(token,idBodyRequest)

    //ACCEPT BID
    suspend fun acceptBid(token:String,idBodyRequest: IdBodyRequest)=
        RetrofitInstance.bidApi.acceptBid(token,idBodyRequest)


    //DECLINE BID
    suspend fun declineBid(token:String,idBodyRequest: IdBodyRequest)=
        RetrofitInstance.bidApi.declineBid(token,idBodyRequest)


    /**************** AS A SERVICE PROVIDER ****************/
    //MAKE BID
    suspend fun makeBid(token:String,makeBidRequest: MakeBidRequest)=
        RetrofitInstance.bidApi.makeBid(token,makeBidRequest)

    //GET SENT BIDS
    suspend fun getSentBids(token:String)=
        RetrofitInstance.bidApi.getSentBids(token)


    //DELETE BID
    suspend fun deleteBid(token:String,idBodyRequest: IdBodyRequest)=
        RetrofitInstance.bidApi.deleteBid(token,idBodyRequest)

    //GET MY BID ON A SPECIFIC REQUEST
    suspend fun getMyBid(token:String,idBodyRequest: IdBodyRequest)=
        RetrofitInstance.bidApi.getMyBidOnRequest(token,idBodyRequest)

}