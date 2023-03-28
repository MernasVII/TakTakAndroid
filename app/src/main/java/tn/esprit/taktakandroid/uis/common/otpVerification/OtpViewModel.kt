package tn.esprit.taktakandroid.uis.common.otpVerification

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.models.sendOtp.SendOtpRequest
import tn.esprit.taktakandroid.models.sendOtp.SendOtpResponse
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class OtpViewModel(private val repository: UserRepository, application: Application) :
    AndroidViewModel(application) {


    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private val _otpError = MutableLiveData<String>()
    val otpError: LiveData<String>
        get() = _otpError

    fun setEmail(email: String) {
        _email.value = email
    }

    private val _otpValue = MutableLiveData<String>()
    val otpValue: LiveData<String>
        get() = _otpValue

    fun setOtp(email: String) {
        _otpValue.value = email
    }

    private val _verifyOtpResult = MutableLiveData<Resource<String>>()
    val verifyOtpResult: LiveData<Resource<String>>
        get() = _verifyOtpResult

    private val _sendOtpResult = MutableLiveData<Resource<SendOtpResponse>>()
    val sendOtpResult: LiveData<Resource<SendOtpResponse>>
        get() = _sendOtpResult

    fun verifyOtp() {
        if (isOTPValid()) {
            _verifyOtpResult.postValue(Resource.Loading())

            viewModelScope.launch {
                delay(1000L)
                val otpStored = AppDataStore.readString(Constants.OTP)
                otpStored?.let { otp ->
                    _verifyOtpResult.postValue(handleOtpValue(otp))
                }
            }
        }

    }

    fun sendOtp() {

        _sendOtpResult.postValue(Resource.Loading())
        viewModelScope.launch {
            try {
                val generatedOtp = repository.generateOTP()
                val sendOtpRequest = SendOtpRequest(email.value!!, generatedOtp)
                val result = repository.sendOtp(sendOtpRequest)
                _sendOtpResult.postValue(handleResponse(generatedOtp, result))

            } catch (e: java.lang.Exception) {
                _sendOtpResult.postValue(Resource.Error("Failed to connect"))
            }

        }

    }

    private fun handleResponse(
        otp: String,
        response: Response<SendOtpResponse>
    ): Resource<SendOtpResponse> {
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


    private fun handleOtpValue(otpStored: String): Resource<String> {
        if (otpStored == _otpValue.value) {
            return Resource.Success("OTP sent successfully!")
        }
        return Resource.Error("Wrong OTP!")
    }

    private fun isOTPValid(): Boolean {
        if (_otpValue.value == null || _otpValue.value!!.length != 4) {
            _otpError.postValue("OTP cannot be empty!")
            return false
        }
        return true
    }


}