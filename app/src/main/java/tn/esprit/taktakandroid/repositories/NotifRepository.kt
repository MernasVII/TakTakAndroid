package tn.esprit.taktakandroid.repositories

import tn.esprit.taktakandroid.api.RetrofitInstance
import tn.esprit.taktakandroid.models.requests.IdBodyRequest

class NotifRepository {
    //GET NOTIFS
    suspend fun getNotifsList(token:String)=
        RetrofitInstance.notifApi.getNotifs(token)

    //MARK READ
    suspend fun markRead(token:String,idBodyRequest: IdBodyRequest)=
        RetrofitInstance.notifApi.markRead(token,idBodyRequest)

    suspend fun countNotifs(token:String)=
        RetrofitInstance.notifApi.countNotifs(token)
}