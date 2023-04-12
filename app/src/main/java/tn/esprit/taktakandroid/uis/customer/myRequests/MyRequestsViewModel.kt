package tn.esprit.taktakandroid.uis.customer.myRequests

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response
import tn.esprit.taktakandroid.models.entities.Request
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.models.responses.LoginResponse
import tn.esprit.taktakandroid.models.responses.SPsResponse
import tn.esprit.taktakandroid.models.responses.UserReqResponse
import tn.esprit.taktakandroid.repositories.RequestsRepository
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class MyRequestsViewModel(private val requestsRepository: RequestsRepository
) : ViewModel() {

    private val _getMyRequestsResult= MutableLiveData<Resource<UserReqResponse>>()
    val myRequestsResult: LiveData<Resource<UserReqResponse>>
            get() = _getMyRequestsResult



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
            _getMyRequestsResult.postValue(handleSPsResponse(response))
        } catch (exception: Exception) {
            _getMyRequestsResult.postValue(Resource.Error("Server connection failed!"))
        }
    }

    private fun handleSPsResponse(response: Response<UserReqResponse>): Resource<UserReqResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                _requests.postValue(resultResponse.myRequests)
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun filter(filtredVal:String){
        _tempRequests.value!!.clear()
        val templst= mutableListOf<Request>()
        if(!_requests.value.isNullOrEmpty() && !filtredVal.isNullOrEmpty()){
            _requests.value!!.forEach {
                if(it.tos.contains(filtredVal, ignoreCase = true))  templst.add(it)
            }
            _tempRequests.postValue(templst)
        }
        else{
            _tempRequests.postValue(_requests.value!!.toMutableList())
        }
    }


}