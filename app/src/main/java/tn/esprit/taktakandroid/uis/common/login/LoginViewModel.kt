package tn.esprit.taktakandroid.uis.common.login

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.models.login.LoginRequest
import tn.esprit.taktakandroid.models.login.LoginResponse
import tn.esprit.taktakandroid.models.sendOtp.SendOtpRequest
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource


class LoginViewModel(private val repository: UserRepository, application: Application) :
    AndroidViewModel(
        application
    ) {
    private val _test = MutableLiveData<String>()
    val test: LiveData<String>
        get() = _test


    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email

    private val _emailError = MutableLiveData<String>()
    val emailError: LiveData<String>
        get() = _emailError

    private val _password = MutableLiveData<String>()
    val password: LiveData<String>
        get() = _password

    private val _passwordError = MutableLiveData<String>()
    val passwordError: LiveData<String>
        get() = _passwordError

    private val _loginResult = MutableLiveData<Resource<LoginResponse>>()
    val loginResult: LiveData<Resource<LoginResponse>>
        get() = _loginResult

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun removePwdError() {
        _passwordError.value = ""
    }

    fun removeEmailError() {
        _emailError.value = ""
    }

    fun setTest(msg: String) {
        _test.value = msg
    }

    fun login() {
        val email = email.value
        val password = password.value
        val isEmailValid = isEmailValid(email)
        val isPwdValid = isPwdValid(password)
        if (isEmailValid && isPwdValid) {
            try {
                _loginResult.postValue(Resource.Loading())
                viewModelScope.launch {
                    val result = repository.login(LoginRequest(email, password))
                    _loginResult.postValue(handleResponse(result))
                }

            } catch (e: java.lang.Exception) {
                _loginResult.postValue(Resource.Error("Failed to connect"))
            }

        }


    }

    private fun handleResponse(response: Response<LoginResponse>): Resource<LoginResponse> {
        if (response.isSuccessful) {

            response.body()?.let { resultResponse ->
                //  AppDataStore.init(getApplication<Application>().applicationContext)
                viewModelScope.launch(Dispatchers.IO) {
                    AppDataStore.writeString(Constants.AUTH_TOKEN, resultResponse.token!!)
                }
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

    private fun isPwdValid(pwd: String?): Boolean {
        if (pwd == null || pwd.isEmpty() || pwd.length < 8) {
            _passwordError.postValue("Password needs 8+ characters and a mix of letters and numbers for security")
            return false
        }
        return true
    }

}

