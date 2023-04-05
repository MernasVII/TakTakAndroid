package tn.esprit.taktakandroid.uis.common.registerTwo

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.models.MessageResponse
import tn.esprit.taktakandroid.models.SignUpRequest
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.Resource


class RegisterTwoViewModel(private val repository: UserRepository) : ViewModel(
) {

    private val _email = MutableLiveData<String>()
    val email: LiveData<String>
        get() = _email


    private val _password = MutableLiveData<String>()
    val password: LiveData<String>
        get() = _password


    private val _firstname = MutableLiveData<String>()
    val firstname: LiveData<String>
        get() = _firstname


    private val _lastname = MutableLiveData<String>()
    val lastname: LiveData<String>
        get() = _lastname


    private val _address = MutableLiveData<String>()
    val address: LiveData<String>
        get() = _address

    private val _speciality = MutableLiveData<String>()
    val speciality: LiveData<String>
        get() = _speciality

    private val _specialityError = MutableLiveData<String>()
    val specialityError: LiveData<String>
        get() = _specialityError

    private val _cin = MutableLiveData<String>()
    val cin: LiveData<String>
        get() = _cin

    private val _cinError = MutableLiveData<String>()
    val cinError: LiveData<String>
        get() = _cinError


    private val _tos = MutableLiveData<ArrayList<String>>()
    val tos: LiveData<ArrayList<String>>
        get() = _tos

    private val _tosError = MutableLiveData<String>()
    val tosError: LiveData<String>
        get() = _tosError

    private val _workDays = MutableLiveData<ArrayList<String>>()
    val workDays: LiveData<ArrayList<String>>
        get() = _workDays

    private val _workDaysError = MutableLiveData<String>()
    val workDaysError: LiveData<String>
        get() = _workDaysError


    private val _signUpResult = MutableLiveData<Resource<MessageResponse>>()
    val signUpResult: LiveData<Resource<MessageResponse>>
        get() = _signUpResult

    init {
        _tos.value = arrayListOf()
        _workDays.value = arrayListOf()
    }

    fun setSpeciality(spec: String) {
        _speciality.value = spec
    }

    fun setCin(cin: String) {
        _cin.value = cin
    }

    fun addTos(tos: String) {
        _tos.value?.add(tos)
    }

    fun deleteTos(tos: String) {
        _tos.value?.remove(tos)
    }

    fun addDay(day: String) {
        _workDays.value?.add(day)
    }

    fun deleteDay(day: String) {
        _workDays.value?.remove(day)
    }

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

    fun removeSpecialityError() {
        _specialityError.value = ""
    }

    fun removeCinError() {
        _cinError.value = ""
    }



    private val handler = CoroutineExceptionHandler { _, _ ->
        _signUpResult.postValue(Resource.Error("Failed to connect"))
    }


    fun signUp() {


        val email = _email.value
        val password = _password.value
        val firstname = _firstname.value
        val lastname = _lastname.value
        val address = _address.value
        val speciality = _speciality.value
        val cin = _cin.value
        val tos = _tos.value
        val workDays = _workDays.value

        if (fieldsValidation(speciality, cin, tos, workDays)) {
            try {
                _signUpResult.postValue(Resource.Loading())
                viewModelScope.launch(handler) {
                    val result = repository.signUp(
                        SignUpRequest(
                            firstname!!,
                            lastname!!,
                            password!!,
                            address!!,
                            email!!,
                            cin!!,
                            speciality,
                            tos!!,
                            workDays!!
                        )
                    )
                    _signUpResult.postValue(handleResponse(result))
                }
            } catch (e: java.lang.Exception) {
                _signUpResult.postValue(Resource.Error("Failed to connect"))
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


    private fun isSpecialityValid(speciality: String?): Boolean {
        if (speciality.isNullOrEmpty()) {
            _specialityError.postValue("Speciality cannot be empty!")
            return false
        }
        return true
    }

    private fun isCinValid(cin: String?): Boolean {
        if (cin.isNullOrEmpty() || cin.length != 8) {
            _cinError.postValue("Enter a valid CIN number!")
            return false
        }
        return true
    }

    private fun isTosValid(tos: ArrayList<String>?): Boolean {
        if (tos.isNullOrEmpty()) {
            _tosError.postValue("Types of services cannot be empty!")
            return false
        }
        return true
    }

    private fun isWorkDaysValid(workDays: ArrayList<String>?): Boolean {
        if (workDays.isNullOrEmpty()) {
            _workDaysError.postValue("Working days cannot be empty!")
            return false
        }
        return true
    }

    private fun fieldsValidation(
        speciality: String?, cin: String?, tos: ArrayList<String>?, workDays: ArrayList<String>?
    ): Boolean {

        val isSpecialityValid = isSpecialityValid(speciality)
        val isCinValid = isCinValid(cin)
        val isTosValid = isTosValid(tos)
        val isWorkDaysValid = isWorkDaysValid(workDays)
        return isSpecialityValid && isCinValid && isTosValid && isWorkDaysValid

    }
}

