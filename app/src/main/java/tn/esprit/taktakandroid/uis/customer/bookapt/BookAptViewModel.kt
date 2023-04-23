package tn.esprit.taktakandroid.uis.customer.bookapt

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.models.requests.BookAptRequest
import tn.esprit.taktakandroid.models.responses.MessageResponse
import tn.esprit.taktakandroid.repositories.AptRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource
import tn.esprit.taktakandroid.utils.SocketService

class BookAptViewModel(
    private val repository: AptRepository,
    private val sp:User
) : ViewModel() {
    val bookAptRes: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()

    private val _date = MutableLiveData<String>()
    val date: LiveData<String>
        get() = _date

    private val _dateError = MutableLiveData<String>()
    val dateError: LiveData<String>
        get() = _dateError

    private val _location = MutableLiveData<String>()
    val location: LiveData<String>
        get() = _location

    private val _locationError = MutableLiveData<String>()
    val locationError: LiveData<String>
        get() = _locationError


    private val _desc = MutableLiveData<String>()
    val desc: LiveData<String>
        get() = _desc

    private val _descError = MutableLiveData<String>()
    val descError: LiveData<String>
        get() = _descError


    private val _tos = MutableLiveData<String>()
    val tos: LiveData<String>
        get() = _tos

    private val _tosError = MutableLiveData<String>()
    val tosError: LiveData<String>
        get() = _tosError

    fun setLocation(location: String) {
        _location.value = location
    }

    fun removeLocationError() {
        _locationError.value = ""
    }

    fun setDate(date: String) {
        _date.value = date
    }

    fun removeDateError() {
        _dateError.value = ""
    }

    fun setDesc(desc: String) {
        _desc.value = desc
    }

    fun removeDescError() {
        _descError.value = ""
    }

    fun setTos(tos: String) {
        _tos.value = tos
    }

    fun removeTosError() {
        _tosError.value = ""
    }

    private val handler = CoroutineExceptionHandler { _, _ ->
        bookAptRes.postValue(Resource.Error("Server connection failed!"))
    }

    fun bookApt() = viewModelScope.launch {
        val date = _date.value
        val location = _location.value
        val desc = _desc.value
        val tos = _tos.value
        if (fieldsValidation(date,location, tos, desc)) {
            try {
                bookAptRes.postValue(Resource.Loading())
                val token = AppDataStore.readString(Constants.AUTH_TOKEN)
                viewModelScope.launch(handler) {
                    val response = repository.bookApt(
                        "Bearer $token", BookAptRequest(
                            date!!,
                            desc!!,
                            location!!,
                            sp._id!!,
                            false,
                            tos!!
                        )
                    )
                    bookAptRes.postValue(handleResponse(response))
                }

            } catch (e: Exception) {
                bookAptRes.postValue(Resource.Error("Server connection failed!"))
            }

        }
    }


    private fun handleResponse(response: Response<MessageResponse>): Resource<MessageResponse> {
        if (response.isSuccessful) {
            viewModelScope.launch {
                val currUserID = AppDataStore.readString(Constants.USER_ID)
                val msg = "${sp._id}/ booked an appointment!/$currUserID"
                SocketService.sendMessage(msg)
            }
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))
    }


    private fun fieldsValidation(
        date: String?,location: String?, tos: String?, desc: String?
    ): Boolean {
        val isDateValid = isDateValid(date)
        val isLocationValid = isLocationValid(location)
        val isDescValid = isDescValid(desc)
        val isTosValid = isTosValid(tos)
        return isDateValid && isLocationValid && isTosValid && isDescValid
    }

    private fun isDateValid(date: String?): Boolean {
        if (date.isNullOrEmpty()) {
            _dateError.postValue("Date cannot be empty!")
            return false
        }
        return true
    }

    private fun isLocationValid(location: String?): Boolean {
        if (location.isNullOrEmpty()) {
            _locationError.postValue("Location cannot be empty!")
            return false
        }
        return true
    }

    private fun isTosValid(tos: String?): Boolean {
        if (tos.isNullOrEmpty()) {
            _tosError.postValue("Types of services cannot be empty!")
            return false
        }
        return true
    }

    private fun isDescValid(desc: String?): Boolean {
        if (desc.isNullOrEmpty() || desc.length < 3) {
            _descError.postValue("Description should at least contain 3 characters!")
            return false
        }
        return true
    }

}