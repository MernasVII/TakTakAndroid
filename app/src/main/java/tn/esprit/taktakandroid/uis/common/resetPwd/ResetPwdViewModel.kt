package tn.esprit.taktakandroid.uis.common.resetPwd

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.models.resetPwd.ResetPwdRequest
import tn.esprit.taktakandroid.models.resetPwd.ResetPwdResponse
import tn.esprit.taktakandroid.models.sendOtp.SendOtpRequest
import tn.esprit.taktakandroid.models.sendOtp.SendOtpResponse
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource


class ResetPwdViewModel(private val repository: UserRepository) :
   ViewModel(

    ) {

    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private val _emailError = MutableLiveData<String>()
    val emailError: LiveData<String>
        get() = _emailError

    private val _resetPwdResult = MutableLiveData<Resource<ResetPwdResponse>>()
    val resetPwdResult: LiveData<Resource<ResetPwdResponse>>
        get() = _resetPwdResult

    private val _password = MutableLiveData<String>()
    val password: LiveData<String>
        get() = _password

    private val _passwordError = MutableLiveData<String>()
    val passwordError: LiveData<String>
        get() = _passwordError

    fun setEmail(email: String) {
        _email.value = email
    }
    fun setPassword(password: String) {
        _password.value = password
    }
    fun removePwdError() {
        _passwordError.value = ""
    }

    private val handler = CoroutineExceptionHandler { _, _ ->
        _resetPwdResult.postValue(Resource.Error("Failed to connect"))
    }
    fun resetPwd() {
        val pwd = _password.value
        val isPwdValid = isPwdValid(pwd)
        if (isPwdValid) {
            _resetPwdResult.postValue(Resource.Loading())
            viewModelScope.launch(handler) {
                try {
                    val resetPwdRequest = ResetPwdRequest(_email.value, pwd)
                    val result = repository.resetPwd(resetPwdRequest)
                    _resetPwdResult.postValue(handleResponse(result))

                }
                catch (e :java.lang.Exception){
                    _resetPwdResult.postValue(Resource.Error("Failed to connect"))
                }

            }
        }

    }

    private fun handleResponse(response: Response<ResetPwdResponse>): Resource<ResetPwdResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))


    }

    private fun isPwdValid(pwd: String?): Boolean {
        if (pwd == null || pwd.isEmpty() || pwd.length < 8) {
            _passwordError.postValue("Password needs 8+ characters and a mix of letters and numbers for security")
            return false
        }
        return true
    }





}