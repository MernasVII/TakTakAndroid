package tn.esprit.taktakandroid.uis.common.login

import android.app.Application
import android.content.Intent
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.models.requests.SignUpRequest
import tn.esprit.taktakandroid.models.requests.LoginRequest
import tn.esprit.taktakandroid.models.responses.LoginResponse
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource


class LoginViewModel(private val repository: UserRepository, private val app: Application) :
    AndroidViewModel(app) {



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

    private val handler = CoroutineExceptionHandler { _, _ ->
        _loginResult.postValue(Resource.Error("Failed to connect"))
    }

    fun login() {
        val email = _email.value
        val password = _password.value
        val isEmailValid = isEmailValid(email)
        val isPwdValid = isPwdValid(password)
        if (isEmailValid && isPwdValid) {
            try {
                _loginResult.postValue(Resource.Loading())
                viewModelScope.launch(handler) {
                    val result = repository.login(LoginRequest(email, password))
                    _loginResult.postValue(handleResponse(result))
                }
            } catch (e: Exception) {
                _loginResult.postValue(Resource.Error("Failed to connect"))
            }
        }
    }

    private fun handleResponse(response: Response<LoginResponse>): Resource<LoginResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                viewModelScope.launch(Dispatchers.IO) {
                    AppDataStore.writeString(Constants.AUTH_TOKEN, resultResponse.token!!)
                    AppDataStore.writeString(Constants.CIN, resultResponse.cin!!)
                    AppDataStore.writeString(Constants.USER_ID,resultResponse.id!!)
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
        if (pwd == null || pwd.isEmpty() ) {
            _passwordError.postValue("Password cannot be empty!")
            return false
        }
        return true
    }


    fun googleSignIn():Intent{
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestId()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(app.applicationContext, gso)
        return mGoogleSignInClient.signInIntent

    }

     fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val email = account.email
            val firstName = account.givenName
            val lastname = account.familyName
            val pic=account.photoUrl
            Log.d(TAG, "handleGoogleSignInResult: $pic")
            //TODO save pic url
            val signUpRequest = SignUpRequest(firstname = firstName!!,lastname =lastname!!,email =email!!)
            try {
                _loginResult.postValue(Resource.Loading())
                viewModelScope.launch(handler) {
                    val result = repository.loginWithGoogle(signUpRequest)
                    _loginResult.postValue(handleResponse(result))
                }
            } catch (e: Exception) {
                _loginResult.postValue(Resource.Error("Failed to connect"))
            }
        } catch (e: Exception) {
            Toast.makeText(app.applicationContext, "Couldn't sign in!", Toast.LENGTH_SHORT).show()
        }
    }




}

