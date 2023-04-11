package tn.esprit.taktakandroid.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import tn.esprit.taktakandroid.models.responses.AptsResponse

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

}