package tn.esprit.taktakandroid.repositories

import tn.esprit.taktakandroid.api.RetrofitInstance
import tn.esprit.taktakandroid.models.requests.AddReqRequest

class RequestsRepository {

    suspend fun getMyRequests(token:String)=
        RetrofitInstance.requestApi.getMyRequests(token)

    suspend fun addRequest(token:String,request:AddReqRequest)=
        RetrofitInstance.requestApi.addRequest(token,request)
}