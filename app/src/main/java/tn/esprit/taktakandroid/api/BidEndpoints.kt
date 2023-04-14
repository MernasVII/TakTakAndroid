package tn.esprit.taktakandroid.api

import retrofit2.Response
import retrofit2.http.*
import tn.esprit.taktakandroid.models.MessageResponse
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.models.requests.MakeBidRequest
import tn.esprit.taktakandroid.models.responses.MyBidOnRequestResponse
import tn.esprit.taktakandroid.models.responses.ReceivedBidsResponse
import tn.esprit.taktakandroid.models.responses.SentBidsResponse

interface BidEndpoints {
    /**************** AS A CUSTOMER ****************/
    //GET RECEIVED BIDS
    @GET("bid/getReceivedBids")
    suspend fun getReceivedBids(
        @Header("Authorization") token: String
    ): Response<ReceivedBidsResponse>

    //ACCEPT BID
    @PUT("bid/acceptBid")
    suspend fun acceptBid(
        @Header("Authorization") token: String,
        @Body request: IdBodyRequest
    ): Response<MessageResponse>

    //DECLINE BID
    @PUT("bid/declineBid")
    suspend fun declineBid(
        @Header("Authorization") token: String,
        @Body request: IdBodyRequest
    ): Response<MessageResponse>

    /**************** AS A SERVICE PROVIDER ****************/
    //MAKE BID
    @POST("bid/createBid")
    suspend fun makeBid(
        @Header("Authorization") token: String,
        @Body request: MakeBidRequest
    ): Response<MessageResponse>

    //GET SENT BIDS
    @GET("bid/getSentBids")
    suspend fun getSentBids(
        @Header("Authorization") token: String
    ): Response<SentBidsResponse>

    //DELETE BID
    @DELETE("bid/deleteBid")
    suspend fun deleteBid(
        @Header("Authorization") token: String,
        @Body request: IdBodyRequest
    ): Response<MessageResponse>

    //GET MY BID ON A SPECIFIC REQUEST
    @POST("bid/getMyBid")
    suspend fun getMyBidOnRequest(
        @Header("Authorization") token: String,
        @Body request: IdBodyRequest
    ): Response<MyBidOnRequestResponse>
}