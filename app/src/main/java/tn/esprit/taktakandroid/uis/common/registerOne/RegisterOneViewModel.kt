package tn.esprit.taktakandroid.uis.common.registerOne

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.models.responses.MessageResponse
import tn.esprit.taktakandroid.models.requests.SignUpRequest
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.Resource


class RegisterOneViewModel(private val repository: UserRepository,private val app: Application) :
    AndroidViewModel(application = app
    ) {


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

    private val _address = MutableLiveData<String>()
    val address: LiveData<String>
        get() = _address

    private val _addressError = MutableLiveData<String>()
    val addressError: LiveData<String>
        get() = _addressError

    private val _signUpResult = MutableLiveData<Resource<MessageResponse>>()
    val signUpResult: LiveData<Resource<MessageResponse>>
        get() = _signUpResult

    fun setEmail(email: String) {
        _email.value = email
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun setFirstname(firstname: String) {
        _firstname.value = firstname
    }

    fun setLastname(lastname: String) {
        _lastname.value = lastname
    }

    fun setAddress(address: String) {
        _address.value = address
    }

    fun removePwdError() {
        _passwordError.value = ""
    }

    fun removeEmailError() {
        _emailError.value = ""
    }

    fun removeFirstnameError() {
        _firstnameError.value = ""
    }

    fun removeLastnameError() {
        _lastnameError.value = ""
    }

    fun removeAddressError() {
        _addressError.value = ""
    }

    private val handler = CoroutineExceptionHandler { _, _ ->
        _signUpResult.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
    }


    fun signUp() {

        val email = _email.value
        val password = _password.value
        val firstname = _firstname.value
        val lastname = _lastname.value
        val address = _address.value

        if (fieldsValidation(firstname, lastname, password, address, email)) {
            try {
                _signUpResult.postValue(Resource.Loading())
                viewModelScope.launch(handler) {
                    val result = repository.signUp(
                        SignUpRequest(
                            firstname!!,
                            lastname!!,
                            password!!,
                            address!!,
                            email!!
                        )
                    )
                    _signUpResult.postValue(handleResponse(result))
                }

            } catch (e: java.lang.Exception) {
                _signUpResult.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
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

    private fun isEmailValid(email: String?): Boolean {
        if (email == null || !email.matches(Patterns.EMAIL_ADDRESS.toRegex())) {
            _emailError.postValue(app.getString(R.string.invalid_email))
            return false
        }
        return true
    }

    private fun isFirstnameValid(firstname: String?): Boolean {
        if (firstname.isNullOrEmpty()) {
            _firstnameError.postValue(app.getString(R.string.first_name_cant_be_empty))
            return false
        }
        return true
    }

    private fun isLastnameValid(lastname: String?): Boolean {
        if (lastname.isNullOrEmpty()) {
            _lastnameError.postValue(app.getString(R.string.last_name_cant_be_empty))
            return false
        }
        return true
    }

    private fun isAddressValid(address: String?): Boolean {
        if (address.isNullOrEmpty()) {
            _addressError.postValue(app.getString(R.string.address_cant_be_empty))
            return false
        }
        return true
    }

    private fun isPwdValid(pwd: String?): Boolean {
        if (pwd == null || pwd.isEmpty() || !isAlphaNumericWithUppercase(pwd)) {
            _passwordError.postValue(app.getString(R.string.pwd_should_be_8cahars))
            return false
        }
        return true
    }
    private fun isAlphaNumericWithUppercase(input: String): Boolean {
        var hasUppercase = false
        var hasDigit = false
        if(input.length<8) return false
        else{
            for (char in input) {

                if (char.isDigit()) {
                    hasDigit = true

                } else if (char.isUpperCase()) {

                    hasUppercase = true
                }
            }
        }
        return hasUppercase && hasDigit // Return true only if an uppercase letter is present
    }

    fun fieldsValidation(
        firstname: String?,
        lastname: String?,
        hash: String?,
        address: String?,
        email: String?
    ): Boolean {

        val isEmailValid = isEmailValid(email)
        val isPwdValid = isPwdValid(hash)
        val isFirstnameValid = isFirstnameValid(firstname)
        val isLastnameValid = isLastnameValid(lastname)
        val isAddressValid = isAddressValid(address)



        return isEmailValid && isPwdValid && isFirstnameValid && isLastnameValid && isAddressValid
    }


}

