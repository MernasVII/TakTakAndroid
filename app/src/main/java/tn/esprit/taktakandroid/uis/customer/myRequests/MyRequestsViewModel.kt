package tn.esprit.taktakandroid.uis.customer.myRequests

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.models.entities.Request
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.models.requests.DeleteReqRequest
import tn.esprit.taktakandroid.models.responses.LoginResponse
import tn.esprit.taktakandroid.models.responses.MessageResponse
import tn.esprit.taktakandroid.models.responses.SPsResponse
import tn.esprit.taktakandroid.models.responses.UserReqResponse
import tn.esprit.taktakandroid.repositories.RequestsRepository
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class MyRequestsViewModel(private val requestsRepository: RequestsRepository,private val application: Application
) : AndroidViewModel(application) {

    private val _getMyRequestsResult= MutableLiveData<Resource<UserReqResponse>>()
    val myRequestsResult: LiveData<Resource<UserReqResponse>>
            get() = _getMyRequestsResult

    private val _deleteReqResult= MutableLiveData<Resource<MessageResponse>>()
    val deleteReqResult: LiveData<Resource<MessageResponse>>
        get() = _deleteReqResult


    private val _tempRequests = MutableLiveData<MutableList<Request>>()
    val tempRequests: LiveData<MutableList<Request>> = _tempRequests

    private val _requests = MutableLiveData<List<Request>>()
    val requests: LiveData<List<Request>> = _requests



    init {
        _tempRequests.value = mutableListOf()
        _requests.value = listOf()
    }


      fun getMyRequests() = viewModelScope.launch {
        try {
            _getMyRequestsResult.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            val response = requestsRepository.getMyRequests("Bearer $token")
            _getMyRequestsResult.postValue(handleGetResponse(response))
        } catch (exception: Exception) {
            _getMyRequestsResult.postValue(Resource.Error(application.getString(R.string.server_connection_failed)))
        }
    }
    fun deleteRequest(id:String) = viewModelScope.launch {
        try {
            _requests.postValue(listOf())
            _deleteReqResult.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            val response = requestsRepository.deleteRequest("Bearer $token", DeleteReqRequest(id))
            _deleteReqResult.postValue(handleDeleteResponse(response))
       } catch (exception: Exception) {
            _deleteReqResult.postValue(Resource.Error(application.getString(R.string.server_connection_failed)))
        }
    }

    private fun handleDeleteResponse(response: Response<MessageResponse>): Resource<MessageResponse> {
        return if (response.isSuccessful) {
            Resource.Success(MessageResponse(application.getString(R.string.request_deleted)))
        } else{
            Resource.Error(application.getString(R.string.cannot_delete_request))
        }
    }
    private fun handleGetResponse(response: Response<UserReqResponse>): Resource<UserReqResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                _requests.postValue(resultResponse.myRequests)
                return Resource.Success(resultResponse)
            }
        }
        else{
            _requests.postValue(listOf())
            return Resource.Error(response.message())
        }
        return Resource.Error(response.message())
    }

    fun filter(filtredVal:String){
        _tempRequests.value?.clear()
        val templst= mutableListOf<Request>()
        if(!_requests.value.isNullOrEmpty() && !filtredVal.isNullOrEmpty()){
            _requests.value!!.forEach {
                if(it.tos.contains(filtredVal, ignoreCase = true))  templst.add(it)
            }
            _tempRequests.postValue(templst)
        }
        else{
            _tempRequests.postValue(_requests.value?.toMutableList()  )
        }
    }


}