package tn.esprit.taktakandroid.uis.common.userprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response
import tn.esprit.taktakandroid.models.responses.MessageResponse
import tn.esprit.taktakandroid.models.responses.UserProfileResponse
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource
import java.io.File

class UserProfileViewModel(private val userRepository: UserRepository
) : ViewModel() {
    val userProfileRes: MutableLiveData<Resource<UserProfileResponse>> = MutableLiveData()
    val updatePicRes: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()
    private val _deleteUserStatus = MutableLiveData<DeleteUserStatus>()
    val deleteUserStatus: LiveData<DeleteUserStatus> = _deleteUserStatus

    init {
        getUserProfile()
    }

    fun getUserProfile() = viewModelScope.launch {
        try {
            userProfileRes.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            val response = userRepository.getUserProfile("Bearer $token")
            userProfileRes.postValue(handleUserProfileResponse(response))
        } catch (exception: Exception) {
            userProfileRes.postValue(Resource.Error("Server connection failed!"))
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

    fun updatePic(file: File?) = viewModelScope.launch {
        try {
            updatePicRes.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            val response = file?.let { userRepository.updatePic("Bearer $token", it) }
            updatePicRes.postValue(response?.let { handleUpdatePicResponse(it) })
        } catch (exception: Exception) {
            updatePicRes.postValue(Resource.Error("Server connection failed!"))
        }
    }

    private fun handleUpdatePicResponse(response: Response<MessageResponse>): Resource<MessageResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


}