package tn.esprit.taktakandroid.api

import retrofit2.Response
import retrofit2.http.*
import tn.esprit.taktakandroid.models.requests.AddReqRequest
import tn.esprit.taktakandroid.models.requests.DeleteReqRequest
import tn.esprit.taktakandroid.models.responses.*

interface RequestEndpoints {
    @GET("request/getMyRequests")
    suspend fun getMyRequests(
        @Header("Authorization") token: String
    ): Response<UserReqResponse>

    @GET("request/getArchivedRequests")
    suspend fun getMyArchivedRequests(
        @Header("Authorization") token: String
    ): Response<UserArchivedReqResponse>

    @GET("request/getAllRequests")
    suspend fun getAllRequests(
        @Header("Authorization") token: String
    ): Response<AllReqResponse>

    @POST("request/createReq")
    suspend fun addRequest(
        @Header("Authorization") token: String
    ,@Body request:AddReqRequest): Response<MessageResponse>

    @HTTP(method = "DELETE", path = "request/deleteReq", hasBody = true)
    suspend fun deleteRequest(
        @Header("Authorization") token: String
        ,@Body request:DeleteReqRequest): Response<MessageResponse>
}