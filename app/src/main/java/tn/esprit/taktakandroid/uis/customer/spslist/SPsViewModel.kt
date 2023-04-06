package tn.esprit.taktakandroid.uis.customer.spslist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.models.splist.SPsResponse
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class SPsViewModel(private val userRepository: UserRepository
) : ViewModel() {
    private val TAG:String="SPsViewModel"

    val spsResult: MutableLiveData<Resource<SPsResponse>> = MutableLiveData()
    private val _filteredItems = MutableLiveData<List<User>>()
    val filteredItems: LiveData<List<User>> = _filteredItems

    init {
        getSPsList()
    }

    private fun getSPsList(query: String = "") = viewModelScope.launch {
        try {
            spsResult.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            val response = userRepository.getSPsList("Bearer $token")
            spsResult.postValue(handleSPsResponse(response))
            filterItems(query)
        } catch (exception: Exception) {
            spsResult.postValue(Resource.Error("Server connection failed!"))
        }
    }

    private fun handleSPsResponse(response: Response<SPsResponse>): Resource<SPsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun filterItems(query: String) {
        val filteredList = spsResult.value?.data?.users?.filter {
            val name=it.firstname!!+" "+it.lastname!!
            name.contains(query, ignoreCase = true)
        }
        _filteredItems.postValue(filteredList)
    }


}