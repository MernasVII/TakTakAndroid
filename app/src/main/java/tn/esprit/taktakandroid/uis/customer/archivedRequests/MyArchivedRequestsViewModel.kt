package tn.esprit.taktakandroid.uis.customer.archivedRequests

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import retrofit2.Response
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.models.entities.Request
import tn.esprit.taktakandroid.models.responses.UserArchivedReqResponse
import tn.esprit.taktakandroid.models.responses.UserReqResponse
import tn.esprit.taktakandroid.repositories.RequestsRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class MyArchivedRequestsViewModel(private val requestsRepository: RequestsRepository,private val application: Application
) : AndroidViewModel(application) {

    private val _getMyArchivedRequestsResult= MutableLiveData<Resource<UserArchivedReqResponse>>()
    val myArchivedRequestsResult: LiveData<Resource<UserArchivedReqResponse>>
            get() = _getMyArchivedRequestsResult



    private val _tempArchivedRequests = MutableLiveData<MutableList<Request>>()
    val tempArchivedRequests: LiveData<MutableList<Request>> = _tempArchivedRequests

    private val _requestsArchived = MutableLiveData<List<Request>>()
    val requestsArchived: LiveData<List<Request>> = _requestsArchived



    init {
        _tempArchivedRequests.value = mutableListOf()
        _requestsArchived.value = listOf()
    }


      fun getMyArchivedRequests() = viewModelScope.launch {
        try {
            _requestsArchived.postValue(listOf())
            _getMyArchivedRequestsResult.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            val response = requestsRepository.getMyArchivedRequests("Bearer $token")
            _getMyArchivedRequestsResult.postValue(handleResponse(response))
        } catch (exception: Exception) {
            _getMyArchivedRequestsResult.postValue(Resource.Error(application.getString(R.string.server_connection_failed)))
        }
    }

    private fun handleResponse(response: Response<UserArchivedReqResponse>): Resource<UserArchivedReqResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                _requestsArchived.postValue(resultResponse.archivedRequests)
                return Resource.Success(resultResponse)
            }
        }
        else{
            _requestsArchived.postValue(listOf())
            return Resource.Error(response.message())

        }
        return Resource.Error(response.message())
    }

    fun filter(filtredVal:String){
        _tempArchivedRequests.value?.clear()
        val templst= mutableListOf<Request>()
        if(!_requestsArchived.value.isNullOrEmpty() && !filtredVal.isNullOrEmpty()){
            _requestsArchived.value!!.forEach {
                if(it.tos.contains(filtredVal, ignoreCase = true))  templst.add(it)
            }
            _tempArchivedRequests.postValue(templst)
        }
        else{
            _tempArchivedRequests.postValue(_requestsArchived.value?.toMutableList())
        }
    }


}