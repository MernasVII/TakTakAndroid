package tn.esprit.taktakandroid.uis.common.sheets.editprofile

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.models.MessageResponse
import tn.esprit.taktakandroid.models.requests.UpdateProfileRequest
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class EditProfileViewModel (private val repository: UserRepository) :
    ViewModel(){

    val updateProfileRes: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()

    private val _firstname = MutableLiveData<String>()
    val firstname: LiveData<String>
        get() = _firstname

    private val _firstnameError = MutableLiveData<String>()
    val firstnameError: LiveData<String>
        get() = _firstnameError

    private val _lastname = MutableLiveData<String>()
    val lastname: LiveData<String>
        get() = _lastname

    private val _lastnameError = MutableLiveData<String>()
    val lastnameError: LiveData<String>
        get() = _lastnameError

    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private val _emailError = MutableLiveData<String>()
    val emailError: LiveData<String>
        get() = _emailError

    private val _address = MutableLiveData<String>()
    val address: LiveData<String>
        get() = _address

    private val _addressError = MutableLiveData<String>()
    val addressError: LiveData<String>
        get() = _addressError

    fun setFirstname(firstname: String) {
        _firstname.value = firstname
    }

    fun removeFirstnameError() {
        _firstnameError.value = ""
    }

    fun setLastname(lastname: String) {
        _lastname.value = lastname
    }

    fun removeLastnameError() {
        _lastnameError.value = ""
    }

    fun setAddress(address: String) {
        _address.value = address
    }

    fun removeAddressError() {
        _addressError.value = ""
    }

    private val handler = CoroutineExceptionHandler { _, _ ->
        updateProfileRes.postValue(Resource.Error("Server connection failed!"))
    }

    fun updateProfile() = viewModelScope.launch {
        val firstname = _firstname.value
        val lastname = _lastname.value
        val address = _address.value
        if (fieldsValidation(firstname, lastname, address)) {
            try {
                updateProfileRes.postValue(Resource.Loading())
                val token = AppDataStore.readString(Constants.AUTH_TOKEN)
                viewModelScope.launch(handler) {
                    val response = repository.updateProfile("Bearer $token", UpdateProfileRequest(
                        firstname!!,
                        lastname!!,
                        address!!
                    )
                    )
                    updateProfileRes.postValue(handleResponse(response))
                }

            } catch (e: Exception) {
                updateProfileRes.postValue(Resource.Error("Server connection failed!"))
            }

        }
    }


    private fun handleResponse(response: Response<MessageResponse>): Resource<MessageResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))


    }

    private fun isFirstnameValid(firstname: String?): Boolean {
        if (firstname.isNullOrEmpty()) {
            _firstnameError.postValue("Firstname cannot be empty!")
            return false
        }
        return true
    }

    private fun isLastnameValid(lastname: String?): Boolean {
        if (lastname.isNullOrEmpty()) {
            _lastnameError.postValue("Lastname cannot be empty!")
            return false
        }
        return true
    }

    private fun isAddressValid(address: String?): Boolean {
        if (address.isNullOrEmpty() || address.length<3) {
            _addressError.postValue("Address should contain at least 3 characters!")
            return false
        }
        return true
    }

    private fun fieldsValidation(
        firstname: String?,
        lastname: String?,
        address: String?
    ): Boolean {
        val isFirstnameValid = isFirstnameValid(firstname)
        val isLastnameValid = isLastnameValid(lastname)
        val isAddressValid = isAddressValid(address)
        return isFirstnameValid && isLastnameValid && isAddressValid
    }

}