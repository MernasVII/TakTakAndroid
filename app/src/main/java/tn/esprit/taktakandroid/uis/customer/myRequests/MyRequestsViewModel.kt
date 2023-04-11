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
    private val _filteredItems = MutableLiveData<List<Request>>()
    val filteredItems: LiveData<List<Request>> = _filteredItems


    init {
        getMyRequests()
    }

     private fun getMyRequests(query: String = "") = viewModelScope.launch {
        try {
            _getMyRequestsResult.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            val response = requestsRepository.getMyRequests("Bearer $token")
            _getMyRequestsResult.postValue(handleSPsResponse(response))
            filterItems(query)
        } catch (exception: Exception) {
            _getMyRequestsResult.postValue(Resource.Error("Server connection failed!"))
        }
    }

    private fun handleSPsResponse(response: Response<UserReqResponse>): Resource<UserReqResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun filterItems(query: String) {
        val filteredList = _getMyRequestsResult.value?.data?.myRequests?.filter {
            val name=it.tos!!
            name.contains(query, ignoreCase = true)
        }
        _filteredItems.postValue(filteredList)
    }


}