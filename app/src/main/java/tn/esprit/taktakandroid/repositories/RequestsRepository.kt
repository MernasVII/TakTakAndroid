package tn.esprit.taktakandroid.repositories

import tn.esprit.taktakandroid.api.RetrofitInstance
import tn.esprit.taktakandroid.models.requests.AddReqRequest
import tn.esprit.taktakandroid.models.requests.DeleteReqRequest

class RequestsRepository {

    suspend fun getMyRequests(token:String)=
        RetrofitInstance.requestApi.getMyRequests(token)
    suspend fun getMyArchivedRequests(token:String)=
        RetrofitInstance.requestApi.getMyArchivedRequests(token)
    suspend fun getAllRequests(token:String)=
        RetrofitInstance.requestApi.getAllRequests(token)

    suspend fun addRequest(token:String,request:AddReqRequest)=
        RetrofitInstance.requestApi.addRequest(token,request)
    suspend fun deleteRequest(token:String,request:DeleteReqRequest)=
        RetrofitInstance.requestApi.deleteRequest(token,request)
}