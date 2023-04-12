package tn.esprit.taktakandroid.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import tn.esprit.taktakandroid.models.requests.AddReqRequest
import tn.esprit.taktakandroid.models.responses.MessageResponse
import tn.esprit.taktakandroid.models.responses.NotifsResponse
import tn.esprit.taktakandroid.models.responses.UserReqResponse

interface RequestEndpoints {
    @GET("request/getMyRequests")
    suspend fun getMyRequests(
        @Header("Authorization") token: String
    ): Response<UserReqResponse>

    @POST("request/createReq")
    suspend fun addRequest(
        @Header("Authorization") token: String
    ,@Body request:AddReqRequest): Response<MessageResponse>
}