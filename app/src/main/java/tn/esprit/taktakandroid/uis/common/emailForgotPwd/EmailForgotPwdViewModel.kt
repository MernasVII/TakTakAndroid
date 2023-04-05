package tn.esprit.taktakandroid.uis.common.emailForgotPwd

import android.util.Patterns
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.models.MessageResponse
import tn.esprit.taktakandroid.models.SendOtpRequest
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource


class EmailForgotPwdViewModel(private val repository: UserRepository) :
    ViewModel(

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
        _sendOtpResult.postValue(Resource.Error("Failed to connect"))
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
                    _sendOtpResult.postValue(Resource.Error("Failed to connect"))
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
            _emailError.postValue("Invalid Email")
            return false
        }
        return true
    }


}