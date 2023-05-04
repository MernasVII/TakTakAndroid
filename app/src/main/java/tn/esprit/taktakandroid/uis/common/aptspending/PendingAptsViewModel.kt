package tn.esprit.taktakandroid.uis.common.aptspending

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.models.entities.Appointment
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.models.requests.AcceptAptRequest
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.models.responses.AptsResponse
import tn.esprit.taktakandroid.models.responses.MessageResponse
import tn.esprit.taktakandroid.repositories.AptRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource
import tn.esprit.taktakandroid.utils.SocketService

class PendingAptsViewModel(
    private val aptRepository: AptRepository
) : ViewModel() {
    private val TAG: String = "PendingAptsViewModel"

    var cin: String? = ""

    private val _getAptsResult = MutableLiveData<Resource<AptsResponse>>()
    val aptsRes: LiveData<Resource<AptsResponse>>
        get() = _getAptsResult

    private val _tempApts = MutableLiveData<MutableList<Appointment>>()
    val tempApts: LiveData<MutableList<Appointment>> = _tempApts

    private val _apts = MutableLiveData<List<Appointment>>()
    val apts: LiveData<List<Appointment>> = _apts

    //val pendingAptsResult: MutableLiveData<Resource<AptsResponse>> = MutableLiveData()
    val acceptAptRes: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()
    val declineAptRes: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()


    init {
        _tempApts.value = mutableListOf()
        _apts.value = listOf()
    }

    private val handler = CoroutineExceptionHandler { _, _ ->
        acceptAptRes.postValue(Resource.Error("Server connection failed!"))
        declineAptRes.postValue(Resource.Error("Server connection failed!"))
    }

    fun getPendingAptsList() = viewModelScope.launch {
        try {
            _apts.postValue(listOf())
            _getAptsResult.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            cin = AppDataStore.readString(Constants.CIN)
            val response: Response<AptsResponse> = if (cin.isNullOrEmpty()) {
                aptRepository.getRequestedPendingApts("Bearer $token")
            } else {
                aptRepository.getReceivedPendingApts("Bearer $token")
            }
            _getAptsResult.postValue(handleAptResponse(response))
        } catch (exception: Exception) {
            _getAptsResult.postValue(Resource.Error("Server connection failed!"))
        }
    }

    fun acceptApt(acceptAptRequest: AcceptAptRequest, customerID: String) = viewModelScope.launch {
        try {
            acceptAptRes.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            viewModelScope.launch(handler) {
                val response = aptRepository.acceptApt("Bearer $token", acceptAptRequest)
                acceptAptRes.postValue(handleAcceptAptResponse(response, customerID))
            }
        } catch (e: Exception) {
            acceptAptRes.postValue(Resource.Error("Server connection failed!"))
        }
    }

    fun declineApt(idBodyRequest: IdBodyRequest, customerID: String) {
        viewModelScope.launch {
            try {
                declineAptRes.postValue(Resource.Loading())
                val token = AppDataStore.readString(Constants.AUTH_TOKEN)
                viewModelScope.launch(handler) {
                    val response = aptRepository.declineApt("Bearer $token", idBodyRequest)
                    declineAptRes.postValue(handleDeclineAptResponse(response, customerID))
                }

            } catch (e: Exception) {
                declineAptRes.postValue(Resource.Error("Server connection failed!"))
            }
        }
    }

    private fun handleAptResponse(response: Response<AptsResponse>): Resource<AptsResponse> {
        if (response.isSuccessful) {

            response.body()?.let { resultResponse ->
                _apts.postValue(resultResponse.appointments)
                return Resource.Success(resultResponse)
            }
        } else {
            _apts.postValue(listOf())
        }
        return Resource.Error(response.message())
    }

    private fun handleAcceptAptResponse(
        response: Response<MessageResponse>,
        customerID: String
    ): Resource<MessageResponse> {
        if (response.isSuccessful) {
            viewModelScope.launch {
                val currUserID = AppDataStore.readString(Constants.USER_ID)
                val msg = "$customerID/ accepted your appointment!/$currUserID"
                SocketService.sendMessage(msg)
            }
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))
    }

    private fun handleDeclineAptResponse(
        response: Response<MessageResponse>,
        customerID: String
    ): Resource<MessageResponse> {
        if (response.isSuccessful) {
            viewModelScope.launch {
                val currUserID = AppDataStore.readString(Constants.USER_ID)
                val msg = "$customerID/ declined your appointment!/$currUserID"
                SocketService.sendMessage(msg)
            }
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))
    }

    fun filter(filtredVal: String, cin: String) {
        _tempApts.value?.clear()
        val templst = mutableListOf<Appointment>()
        if (!_apts.value.isNullOrEmpty() && !filtredVal.isNullOrEmpty()) {
            _apts.value!!.forEach {
                var user: User
                if (cin.isNullOrEmpty()) {
                    user = it.sp
                } else {
                    user = it.customer
                }
                if (user.firstname!!.contains(
                        filtredVal,
                        ignoreCase = true
                    ) || user.lastname!!.contains(
                        filtredVal,
                        ignoreCase = true
                    ) || it.tos!!.contains(filtredVal, ignoreCase = true)
                ) templst.add(it)
            }
            _tempApts.postValue(templst)
        } else {
            _tempApts.postValue(_apts.value?.toMutableList())
        }
    }
}