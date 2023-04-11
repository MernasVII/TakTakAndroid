package tn.esprit.taktakandroid.repositories

import tn.esprit.taktakandroid.api.RetrofitInstance

class NotifRepository {
    //GET NOTIFS
    suspend fun getNotifsList(token:String)=
        RetrofitInstance.notifApi.getNotifs(token)
}