package tn.esprit.taktakandroid.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.models.responses.CountNotifsResponse
import tn.esprit.taktakandroid.models.responses.MessageResponse
import tn.esprit.taktakandroid.models.responses.NotifsResponse

interface NotifEndpoints {
    //GET NOTIFS
    @GET("notif/getnotifs")
    suspend fun getNotifs(
        @Header("Authorization") token: String
    ): Response<NotifsResponse>

    @GET("notif/countNotifs")
    suspend fun countNotifs(
        @Header("Authorization") token: String
    ): Response<CountNotifsResponse>

    //MARK READ
    @PUT("notif/markNotifRead")
    suspend fun markRead(
        @Header("Authorization") token: String,
        @Body request: IdBodyRequest
    ): Response<MessageResponse>
}