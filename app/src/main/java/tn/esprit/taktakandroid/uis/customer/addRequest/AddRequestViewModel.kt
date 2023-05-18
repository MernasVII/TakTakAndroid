package tn.esprit.taktakandroid.uis.customer.addRequest

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.models.entities.Request
import tn.esprit.taktakandroid.models.requests.AddReqRequest
import tn.esprit.taktakandroid.models.responses.LoginResponse
import tn.esprit.taktakandroid.models.responses.MessageResponse
import tn.esprit.taktakandroid.models.responses.UserReqResponse
import tn.esprit.taktakandroid.repositories.RequestsRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class AddRequestViewModel(
    private val requestsRepository: RequestsRepository,
    private val application: Application
) : AndroidViewModel(application) {

    private val _dateTime = MutableLiveData<String>()
    val dateTime: LiveData<String>
        get() = _dateTime

    private val _dateTimeError = MutableLiveData<String>()
    val dateTimeError: LiveData<String>
        get() = _dateTimeError

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

    private val _addReqResult = MutableLiveData<Resource<MessageResponse>>()
    val addReqResult: LiveData<Resource<MessageResponse>>
        get() = _addReqResult


    fun setTos(tos: String) {
        _tos.value = tos
    }

    fun setDesc(desc: String) {
        _desc.value = desc
    }

    fun setDateTime(dateTime: String) {
        _dateTime.value = dateTime
    }

    fun setLocation(location: String) {
        _location.value = location
    }

    fun removeDateTimeError() {
        _dateTimeError.value = ""
    }

    fun removeLocationError() {
        _locationError.value = ""
    }
    fun removeDescError() {
        _descError.value = ""
    }
    fun removeTosError() {
        _tosError.value = ""
    }
    private val handler = CoroutineExceptionHandler { _, _ ->
        _addReqResult.postValue(Resource.Error(application.getString(R.string.server_connection_failed)))
    }

    fun addRequest() {

        val dateTime = _dateTime.value
        val location = _location.value
        val desc = _desc.value
        val tos = _tos.value
        val isDateTimeValid = isDateTimeValid(dateTime)
        val isLocationValid = isLocationValid(location)
        val isDescValid = isDescValid(desc)
        val isTosValid = isTosValid(tos)
        if (isDateTimeValid && isLocationValid && isDescValid && isTosValid) {
            try {
                _addReqResult.postValue(Resource.Loading())

                viewModelScope.launch(handler) {
                    val token = AppDataStore.readString(Constants.AUTH_TOKEN)
                    val response = requestsRepository.addRequest("Bearer $token", AddReqRequest(dateTime,desc,location,tos))
                    _addReqResult.postValue(handleAddReqResponse(response))
                }
            } catch (exception: Exception) {
                _addReqResult.postValue(Resource.Error(application.getString(R.string.server_connection_failed)))

            }
        }
    }

    private fun handleAddReqResponse(response: Response<MessageResponse>): Resource<MessageResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))
    }

    private fun isDateTimeValid(dateTime: String?): Boolean {
        if (dateTime.isNullOrEmpty()) {
            _dateTimeError.postValue(application.getString(R.string.date_cannot_be_empty))
            return false
        }
        return true
    }

    private fun isLocationValid(location: String?): Boolean {
        if (location.isNullOrEmpty()) {
            _locationError.postValue(application.getString(R.string.location_cannot_be_empty))
            return false
        }
        return true
    }

    private fun isDescValid(desc: String?): Boolean {
        if (desc.isNullOrEmpty() || desc.length < 5) {
            _descError.postValue(application.getString(R.string.please_enter_valid_desc))
            return false
        }
        return true
    }

    private fun isTosValid(tos: String?): Boolean {
        if (tos.isNullOrEmpty()) {
            _tosError.postValue(application.getString(R.string.tos_cant_be_empty))
            return false
        }
        return true
    }

}