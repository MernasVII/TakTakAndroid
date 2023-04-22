package tn.esprit.taktakandroid.repositories

import tn.esprit.taktakandroid.api.RetrofitInstance
import tn.esprit.taktakandroid.models.requests.*

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

    //CANCEL APT
    suspend fun cancelApt(token:String,idBodyRequest: IdBodyRequest)=
        RetrofitInstance.aptApi.cancelApt(token,idBodyRequest)

    //BOOK APT
    suspend fun bookApt(token:String,bookAptRequest: BookAptRequest)=
        RetrofitInstance.aptApi.bookApt(token,bookAptRequest)

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

    //POSTPONE APT
    suspend fun postponeApt(token:String,postponeAptRequest: PostponeAptRequest)=
        RetrofitInstance.aptApi.postponeApt(token,postponeAptRequest)

    //GET TIME LEFT TO APT
    /*suspend fun getTimeLeftToApt(token:String,idBodyRequest: IdBodyRequest)=
        RetrofitInstance.aptApi.getTimeLeftToApt(token,idBodyRequest)*/

    //ACCEPT APT
    suspend fun acceptApt(token:String,acceptAptRequest: AcceptAptRequest)=
        RetrofitInstance.aptApi.acceptApt(token,acceptAptRequest)

    //DECLINE APT
    suspend fun declineApt(token:String,idBodyRequest: IdBodyRequest)=
        RetrofitInstance.aptApi.declineApt(token,idBodyRequest)

    //UPDATE APT STATE
    suspend fun updateAptState(token:String,updateAptStateRequest: UpdateAptStateRequest)=
        RetrofitInstance.aptApi.updateAptState(token,updateAptStateRequest)
}