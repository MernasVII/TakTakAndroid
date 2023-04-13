package tn.esprit.taktakandroid.api

import retrofit2.Response
import retrofit2.http.*
import tn.esprit.taktakandroid.models.MessageResponse
import tn.esprit.taktakandroid.models.SignUpRequest
import tn.esprit.taktakandroid.models.requests.*
import tn.esprit.taktakandroid.models.responses.AptsResponse
import tn.esprit.taktakandroid.models.responses.TimeLeftResponse

interface AptEndpoints {
    /**************** AS A CUSTOMER ****************/
    //GET ALL REQUESTED
    @GET("appointment/getAllRequestedApts")
    suspend fun getAllRequestedApts(
        @Header("Authorization") token: String
    ): Response<AptsResponse>

    //GET REQUESTED ACTIVE (ACCEPTED+PENDING)
    @GET("appointment/getRequestedActiveApts")
    suspend fun getRequestedActiveApts(
        @Header("Authorization") token: String
    ): Response<AptsResponse>

    //GET REQUESTED ACCEPTED
    @GET("appointment/getRequestedAcceptedApts")
    suspend fun getRequestedAcceptedApts(
        @Header("Authorization") token: String
    ): Response<AptsResponse>

    //GET REQUESTED PENDING
    @GET("appointment/getRequestedPendingApts")
    suspend fun getRequestedPendingApts(
        @Header("Authorization") token: String
    ): Response<AptsResponse>

    //GET REQUESTED ARCHIVED
    @GET("appointment/getRequestedArchivedApts")
    suspend fun getRequestedArchivedApts(
        @Header("Authorization") token: String
    ): Response<AptsResponse>

    //CANCEL APT
    @PUT("appointment/archiveApt")
    suspend fun cancelApt(
        @Header("Authorization") token: String,
        @Body request: IdBodyRequest
    ): Response<MessageResponse>

    @POST("appointment/bookApt")
    suspend fun bookApt(
        @Header("Authorization") token: String,
        @Body request: BookAptRequest
    ): Response<MessageResponse>

    /**************** AS A SERVICE PROVIDER ****************/
    //GET ALL RECEIVED
    @GET("appointment/getAllReceivedApts")
    suspend fun getAllReceivedApts(
        @Header("Authorization") token: String
    ): Response<AptsResponse>

    //GET RECEIVED ACCEPTED
    @GET("appointment/getReceivedAcceptedApts")
    suspend fun getReceivedAcceptedApts(
        @Header("Authorization") token: String
    ): Response<AptsResponse>

    //GET RECEIVED PENDING
    @GET("appointment/getReceivedPendingApts")
    suspend fun getReceivedPendingApts(
        @Header("Authorization") token: String
    ): Response<AptsResponse>

    //GET RECEIVED ARCHIVED
    @GET("appointment/getReceivedArchivedApts")
    suspend fun getReceivedArchivedApts(
        @Header("Authorization") token: String
    ): Response<AptsResponse>

    //POSTPONE APT
    @PUT("appointment/postponeApt")
    suspend fun postponeApt(
        @Header("Authorization") token: String,
        @Body request: PostponeAptRequest
    ): Response<MessageResponse>

    //ACCEPT APT
    @PUT("appointment/acceptApt")
    suspend fun acceptApt(
        @Header("Authorization") token: String,
        @Body request: AcceptAptRequest
    ): Response<MessageResponse>

    //DECLINE APT
    @PUT("appointment/declineApt")
    suspend fun declineApt(
        @Header("Authorization") token: String,
        @Body request: IdBodyRequest
    ): Response<MessageResponse>

    /**************** COMMON ****************/
    //TIME LEFT TO APT
    /*@POST("appointment/timeLeft")
    suspend fun getTimeLeftToApt(
        @Header("Authorization") token: String,
        @Body request: IdBodyRequest
    ): Response<TimeLeftResponse>*/

    //UPDATE APT STATE
    @PUT("appointment/updateAptState")
    suspend fun updateAptState(
        @Header("Authorization") token: String,
        @Body request: UpdateAptStateRequest
    ): Response<MessageResponse>

}