package tn.esprit.taktakandroid.uis

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response
import tn.esprit.taktakandroid.models.spslist.SPsResponse
import tn.esprit.taktakandroid.models.userprofile.UserProfileResponse
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class HomeViewModel(
    val userRepository: UserRepository
) : ViewModel() {
    val sps: MutableLiveData<Resource<SPsResponse>> = MutableLiveData()
    val userProfile: MutableLiveData<Resource<UserProfileResponse>> = MutableLiveData()

    private val TAG:String="HomeViewModel"

    init {
        getSPsList()
        getUserProfile()
    }

    private fun getSPsList() = viewModelScope.launch {
        try {
            sps.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            val response = userRepository.getSPsList("Bearer $token")
            sps.postValue(handleSPsResponse(response))
        } catch (exception: Exception) {
            Log.d(TAG, "getSPsList: $exception")
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

    private fun getUserProfile() = viewModelScope.launch {
        try {
            userProfile.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            val response = userRepository.getUserProfile("Bearer $token")
            userProfile.postValue(handleUserProfileResponse(response))
        } catch (exception: Exception) {
            Log.d(TAG, "getUserProfile: $exception")
        }
    }


    private fun handleUserProfileResponse(response: Response<UserProfileResponse>): Resource<UserProfileResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


}