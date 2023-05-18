package tn.esprit.taktakandroid.uis.common.sheets.updatepwd

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.models.responses.MessageResponse
import tn.esprit.taktakandroid.models.requests.UpdatePwdRequest
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class UpdatePasswordViewModel  (private val repository: UserRepository,private val application: Application) :
    AndroidViewModel(application){
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
        updatePwdRes.postValue(Resource.Error(application.getString(R.string.server_connection_failed)))
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
                updatePwdRes.postValue(Resource.Error(application.getString(R.string.server_connection_failed)))
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
            _oldPwdError.postValue(application.getString(R.string.old_pwd_cant_be_empty))
            return false
        }
        return true
    }

    private fun isValidNewPwd(newPwd: String?): Boolean {
        if (newPwd == null || newPwd.isEmpty() || newPwd.length < 8) {
            _newPwdError.postValue(application.getString(R.string.pwd_should_be_8cahars))
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