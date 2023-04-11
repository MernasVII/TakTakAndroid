package tn.esprit.taktakandroid.repositories

import tn.esprit.taktakandroid.api.RetrofitInstance

class RequestsRepository {

    suspend fun getMyRequests(token:String)=
        RetrofitInstance.requestApi.getMyRequests(token)
}