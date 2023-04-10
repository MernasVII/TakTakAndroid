package tn.esprit.taktakandroid.repositories

import tn.esprit.taktakandroid.api.RetrofitInstance

class AptRepository {
    /**************** AS A CUSTOMER ****************/
    //GET ALL REQUESTED
    suspend fun getAllRequestedApts(token:String)=
        RetrofitInstance.aptApi.getAllRequestedApts(token)

    //GET REQUESTED ACTIVE (ACCEPTED+PENDING)
    suspend fun getRequestedActiveApts(token:String)=
        RetrofitInstance.aptApi.getRequestedActiveApts(token)

    //GET REQUESTED ACCEPTED
    suspend fun getRequestedAcceptedApts(token:String)=
        RetrofitInstance.aptApi.getRequestedAcceptedApts(token)

    //GET REQUESTED PENDING
    suspend fun getRequestedPendingApts(token:String)=
        RetrofitInstance.aptApi.getRequestedPendingApts(token)

    //GET REQUESTED ARCHIVED
    suspend fun getRequestedArchivedApts(token:String)=
        RetrofitInstance.aptApi.getRequestedArchivedApts(token)

    /**************** AS A SERVICE PROVIDER ****************/
    //GET ALL RECEIVED
    suspend fun getAllReceivedApts(token:String)=
        RetrofitInstance.aptApi.getAllReceivedApts(token)

    //GET RECEIVED ACCEPTED
    suspend fun getReceivedAcceptedApts(token:String)=
        RetrofitInstance.aptApi.getReceivedAcceptedApts(token)

    //GET RECEIVED PENDING
    suspend fun getReceivedPendingApts(token:String)=
        RetrofitInstance.aptApi.getReceivedPendingApts(token)

    //GET RECEIVED ARCHIVED
    suspend fun getReceivedArchivedApts(token:String)=
        RetrofitInstance.aptApi.getReceivedArchivedApts(token)
}