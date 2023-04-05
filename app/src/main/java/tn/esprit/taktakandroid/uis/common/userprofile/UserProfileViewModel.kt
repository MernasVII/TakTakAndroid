package tn.esprit.taktakandroid.uis.common.userprofile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response
import tn.esprit.taktakandroid.models.MessageResponse
import tn.esprit.taktakandroid.models.userprofile.UserProfileResponse
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class UserProfileViewModel(private val userRepository: UserRepository
) : ViewModel() {
    val userProfile: MutableLiveData<Resource<UserProfileResponse>> = MutableLiveData()
    private val _deleteUserStatus = MutableLiveData<DeleteUserStatus>()
    val deleteUserStatus: LiveData<DeleteUserStatus> = _deleteUserStatus

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
            userProfile.postValue(Resource.Error("Failed to connect"))
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

    fun deleteUser() {
        viewModelScope.launch {
            try {
                val token = AppDataStore.readString(Constants.AUTH_TOKEN)
                if (token != null) {
                    userRepository.deleteUser("Bearer $token")
                }
                _deleteUserStatus.value = DeleteUserStatus.Success
            } catch (e: Exception) {
                _deleteUserStatus.value = DeleteUserStatus.Failure(e.message)
            }
        }
    }

    sealed class DeleteUserStatus {
        object Success : DeleteUserStatus()
        data class Failure(val errorMessage: String?) : DeleteUserStatus()
    }
}