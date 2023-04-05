package tn.esprit.taktakandroid.uis.common.userprofile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response
import tn.esprit.taktakandroid.models.userprofile.UserProfileResponse
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class UserProfileViewModel(private val userRepository: UserRepository
) : ViewModel() {
    val userProfile: MutableLiveData<Resource<UserProfileResponse>> = MutableLiveData()

    init {
        getUserProfile()
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