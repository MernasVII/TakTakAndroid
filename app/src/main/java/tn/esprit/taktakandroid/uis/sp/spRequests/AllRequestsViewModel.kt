package tn.esprit.taktakandroid.uis.sp.spRequests

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import retrofit2.Response
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.models.entities.Request
import tn.esprit.taktakandroid.models.responses.AllReqResponse
import tn.esprit.taktakandroid.repositories.RequestsRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

const val TAG = "AllRequestsViewModel"

class AllRequestsViewModel(
    private val requestsRepository: RequestsRepository,private val application: Application
) : AndroidViewModel(application) {

    private val _getAllRequestsResult = MutableLiveData<Resource<AllReqResponse>>()
    val allRequestsResult: LiveData<Resource<AllReqResponse>>
        get() = _getAllRequestsResult


    private val _tempAllRequests = MutableLiveData<MutableList<Request>>()
    val tempAllRequests: LiveData<MutableList<Request>> = _tempAllRequests

    private val _allRequests = MutableLiveData<List<Request>>()
    val allRequests: LiveData<List<Request>> = _allRequests


    init {
        _tempAllRequests.value = mutableListOf()
        _allRequests.value = listOf()
    }


    fun getAllRequests() = viewModelScope.launch {
        try {
            _allRequests.postValue(listOf())
            _getAllRequestsResult.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            val response = requestsRepository.getAllRequests("Bearer $token")
            _getAllRequestsResult.postValue(handleResponse(response))
        } catch (exception: Exception) {
            _getAllRequestsResult.postValue(Resource.Error(application.getString(R.string.server_connection_failed)))
        }
    }

    private fun handleResponse(response: Response<AllReqResponse>): Resource<AllReqResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                _allRequests.postValue(resultResponse.allRequests)
                return Resource.Success(resultResponse)
            }
        }else{
            _allRequests.postValue(listOf())
            return Resource.Error(response.message())
        }
        return Resource.Error(response.message())
    }

    fun filter(filtredVal: String) {
        _tempAllRequests.value?.clear()
        val templst = mutableListOf<Request>()
        if (!_allRequests.value.isNullOrEmpty() && !filtredVal.isNullOrEmpty()) {
            _allRequests.value!!.forEach {
                if (it.tos.contains(filtredVal, ignoreCase = true)) templst.add(it)
            }
            _tempAllRequests.postValue(templst)
        } else {
            _tempAllRequests.postValue(_allRequests.value?.toMutableList())
        }
    }


}