package tn.esprit.taktakandroid.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import tn.esprit.taktakandroid.models.responses.NotifsResponse
import tn.esprit.taktakandroid.models.responses.UserReqResponse

interface RequestEndpoints {
    @GET("request/getMyRequests")
    suspend fun getMyRequests(
        @Header("Authorization") token: String
    ): Response<UserReqResponse>
}