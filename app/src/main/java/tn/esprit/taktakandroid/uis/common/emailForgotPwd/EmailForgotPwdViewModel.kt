package tn.esprit.taktakandroid.uis.common.emailForgotPwd

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.models.responses.MessageResponse
import tn.esprit.taktakandroid.models.requests.SendOtpRequest
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource


class EmailForgotPwdViewModel(private val repository: UserRepository,private val app: Application) :
    AndroidViewModel(
application = app
    ) {

    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private val _emailError = MutableLiveData<String>()
    val emailError: LiveData<String>
        get() = _emailError

    private val _sendOtpResult = MutableLiveData<Resource<MessageResponse>>()
    val sendOtpResult: LiveData<Resource<MessageResponse>>
        get() = _sendOtpResult

    fun setEmail(email: String) {
        _email.value = email
    }

    fun removeEmailError() {
        _emailError.value = ""
    }

    private val handler = CoroutineExceptionHandler { _, _ ->
        _sendOtpResult.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
    }
    fun sendOtp() {
        val email = _email.value
        val isEmailValid = isEmailValid(email)
        if (isEmailValid) {
            _sendOtpResult.postValue(Resource.Loading())
            viewModelScope.launch(handler) {
                try {
                    val generatedOtp = repository.generateOTP()
                    val sendOtpRequest = SendOtpRequest(email!!, generatedOtp)
                    val result = repository.sendOtp(sendOtpRequest)
                    _sendOtpResult.postValue(handleResponse(generatedOtp,result))

                }
                catch (e :java.lang.Exception){
                    _sendOtpResult.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
                }

            }
        }

    }

    private fun handleResponse(otp :String,response: Response<MessageResponse>): Resource<MessageResponse> {
        if (response.isSuccessful) {
            viewModelScope.launch {
                AppDataStore.writeString(Constants.OTP, otp)
            }
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))


    }

    private fun isEmailValid(email: String?): Boolean {
        if (email == null || !email.matches(Patterns.EMAIL_ADDRESS.toRegex())) {
            _emailError.postValue(app.getString(R.string.invalid_email))
            return false
        }
        return true
    }


}