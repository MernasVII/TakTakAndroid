package tn.esprit.taktakandroid.uis.common.sheets.updatepwd

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.models.MessageResponse
import tn.esprit.taktakandroid.models.updatepwd.UpdatePwdRequest
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class UpdatePasswordViewModel  (private val repository: UserRepository) :
    ViewModel(){
    val updatePwdRes: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()

    private val _oldPwd = MutableLiveData<String>()
    val oldPwd: LiveData<String>
        get() = _oldPwd

    private val _oldPwdError = MutableLiveData<String>()
    val oldPwdError: LiveData<String>
        get() = _oldPwdError

    private val _newPwd = MutableLiveData<String>()
    val newPwd: LiveData<String>
        get() = _newPwd

    private val _newPwdError = MutableLiveData<String>()
    val newPwdError: LiveData<String>
        get() = _newPwdError


    fun setOldPwd(oldPwd: String) {
        _oldPwd.value = oldPwd
    }

    fun removeOldPwdError() {
        _oldPwdError.value = ""
    }

    fun setNewPwd(newPwd: String) {
        _newPwd.value = newPwd
    }

    fun removeNewPwdError() {
        _newPwdError.value = ""
    }

    private val handler = CoroutineExceptionHandler { _, _ ->
        updatePwdRes.postValue(Resource.Error("Server connection failed!"))
    }

    fun updatePwd() = viewModelScope.launch {
        val oldPwd = _oldPwd.value
        val newPwd = _newPwd.value
        if (fieldsValidation(oldPwd,newPwd)) {
            try {
                updatePwdRes.postValue(Resource.Loading())
                val token = AppDataStore.readString(Constants.AUTH_TOKEN)
                viewModelScope.launch(handler) {
                    val response = repository.changepwd("Bearer $token", UpdatePwdRequest(
                        oldPwd!!,
                        newPwd!!,
                    )
                    )
                    updatePwdRes.postValue(handleResponse(response))
                }

            } catch (e: Exception) {
                updatePwdRes.postValue(Resource.Error("Server connection failed!"))
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


    private fun isValidOldPwd(oldPwd: String?): Boolean {
        if (oldPwd.isNullOrEmpty()) {
            _oldPwdError.postValue("Old password cannot be empty!")
            return false
        }
        return true
    }

    private fun isValidNewPwd(newPwd: String?): Boolean {
        if (newPwd == null || newPwd.isEmpty() || newPwd.length < 8) {
            _newPwdError.postValue("Password should contain 8 characters at least!")
            return false
        }
        return true
    }

    private fun fieldsValidation(
        oldPwd: String?,
        newPwd: String?
    ): Boolean {
        val isOldPwdValid = isValidOldPwd(oldPwd)
        val isNewPwdValid = isValidNewPwd(newPwd)
        return isOldPwdValid && isNewPwdValid
    }
}