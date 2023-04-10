package tn.esprit.taktakandroid.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import tn.esprit.taktakandroid.models.responses.NotifsResponse

interface NotifEndpoints {
    //GET NOTIFS
    @GET("notif/getnotifs")
    suspend fun getNotifs(
        @Header("Authorization") token: String
    ): Response<NotifsResponse>
}