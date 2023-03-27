package tn.esprit.taktakandroid.uis.common.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import tn.esprit.taktakandroid.models.Login.LoginRequest
import tn.esprit.taktakandroid.models.Login.LoginResponse
import tn.esprit.taktakandroid.repositories.LoginRepository
import tn.esprit.taktakandroid.utils.Resource

class LoginViewModel(val repository: LoginRepository) : ViewModel() {

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

    fun login() {
        val email = email.value
        val password = password.value
        val isEmailValid = isEmailValid(email)
        val isPwdValid = isPwdValid(password)
        if (isEmailValid && isPwdValid) {

            viewModelScope.launch {
                val result = repository.login(LoginRequest(email, password))
                _loginResult.postValue(handleResponse(result))
            }
        }

    }

    private fun handleResponse(response: Response<LoginResponse>): Resource<LoginResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
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