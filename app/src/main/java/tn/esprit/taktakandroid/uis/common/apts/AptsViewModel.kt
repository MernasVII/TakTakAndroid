package tn.esprit.taktakandroid.uis.common.apts

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
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.models.requests.PostponeAptRequest
import tn.esprit.taktakandroid.models.requests.UpdateAptStateRequest
import tn.esprit.taktakandroid.models.responses.AptsResponse
import tn.esprit.taktakandroid.models.responses.MessageResponse
import tn.esprit.taktakandroid.repositories.AptRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class AptsViewModel  (private val aptRepository: AptRepository
) : ViewModel() {
    private val TAG:String="AptsViewModel"

    var cin:String?=""

    private val _getAptsResult= MutableLiveData<Resource<AptsResponse>>()
    val aptsRes: LiveData<Resource<AptsResponse>>
        get() = _getAptsResult

    private val _tempApts = MutableLiveData<MutableList<Appointment>>()
    val tempApts: LiveData<MutableList<Appointment>> = _tempApts

    private val _apts = MutableLiveData<List<Appointment>>()
    val apts: LiveData<List<Appointment>> = _apts

    //val aptsResult: MutableLiveData<Resource<AptsResponse>> = MutableLiveData()
    val cancelAptRes: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()
    val postponeAptRes: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()
    val updateStateAptRes: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()
    //val timeLeftAptRes: MutableLiveData<Resource<TimeLeftResponse>> = MutableLiveData()




    init {
        _tempApts.value = mutableListOf()
        _apts.value = listOf()
    }

    fun getAptsList() = viewModelScope.launch {
        try {
            _apts.postValue(listOf())
            _getAptsResult.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            cin = AppDataStore.readString(Constants.CIN)
            val response : Response<AptsResponse> = if(cin.isNullOrEmpty()){
                aptRepository.getRequestedAcceptedApts("Bearer $token")
            }else{
                aptRepository.getReceivedAcceptedApts("Bearer $token")
            }
            _getAptsResult.postValue(handleAptResponse(response))
        } catch (exception: Exception) {
            _getAptsResult.postValue(Resource.Error("Server connection failed!"))
        }
    }

    fun cancelApt(idBodyRequest: IdBodyRequest) = viewModelScope.launch {
            try {
                cancelAptRes.postValue(Resource.Loading())
                val token = AppDataStore.readString(Constants.AUTH_TOKEN)
                viewModelScope.launch(handler) {
                    val response = aptRepository.cancelApt("Bearer $token", idBodyRequest)
                    cancelAptRes.postValue(handleAptCancelResponse(response))
                }
            } catch (e: Exception) {
                cancelAptRes.postValue(Resource.Error("Server connection failed!"))
            }
    }

    fun postponeApt(postponeAptRequest: PostponeAptRequest) = viewModelScope.launch {
        try {
            postponeAptRes.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            viewModelScope.launch(handler) {
                val response = aptRepository.postponeApt("Bearer $token", postponeAptRequest)
                postponeAptRes.postValue(handleAptPostponeResponse(response))
            }

        } catch (e: Exception) {
            postponeAptRes.postValue(Resource.Error("Server connection failed!"))
        }
    }

    fun updateAptState(updateAptStateRequest: UpdateAptStateRequest) = viewModelScope.launch {
        try {
            updateStateAptRes.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            viewModelScope.launch(handler) {
                val response = aptRepository.updateAptState("Bearer $token", updateAptStateRequest)
                updateStateAptRes.postValue(handleAptStateResponse(response))
            }

        } catch (e: Exception) {
            updateStateAptRes.postValue(Resource.Error("Server connection failed!"))
        }
    }

    /*fun getTimeLeftToApt(idBodyRequest: IdBodyRequest) = viewModelScope.launch {
        timeLeftAptRes.postValue(Resource.Loading())
        val token = AppDataStore.readString(Constants.AUTH_TOKEN)
        val response = aptRepository.getTimeLeftToApt("Bearer $token",idBodyRequest)
        timeLeftAptRes.postValue(handleTimeLeftAptResponse(response))

    }*/

    private fun handleAptResponse(response: Response<AptsResponse>): Resource<AptsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                _apts.postValue(resultResponse.appointments)
                return Resource.Success(resultResponse)
            }
        }else{
            _apts.postValue(listOf())
        }
        return Resource.Error(response.message())
    }

    private val handler = CoroutineExceptionHandler { _, _ ->
        cancelAptRes.postValue(Resource.Error("Server connection failed!"))
        postponeAptRes.postValue(Resource.Error("Server connection failed!"))
        updateStateAptRes.postValue(Resource.Error("Server connection failed!"))
    }

    private fun handleAptStateResponse(response: Response<MessageResponse>): Resource<MessageResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))
    }

    private fun handleAptCancelResponse(response: Response<MessageResponse>): Resource<MessageResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))
    }

    private fun handleAptPostponeResponse(response: Response<MessageResponse>): Resource<MessageResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))
    }



    /*private fun handleTimeLeftAptResponse(response: Response<TimeLeftResponse>): Resource<TimeLeftResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }*/

    fun filter(filtredVal:String,cin:String){
        _tempApts.value?.clear()
        val templst= mutableListOf<Appointment>()
        if(!_apts.value.isNullOrEmpty() && !filtredVal.isNullOrEmpty()){
            _apts.value!!.forEach {
                var user:User
                if(cin.isNullOrEmpty()){
                    user=it.sp
                }else{
                    user=it.customer
                }
                if(user.firstname!!.contains(filtredVal, ignoreCase = true) || user.lastname!!.contains(filtredVal, ignoreCase = true) || it.tos!!.contains(filtredVal, ignoreCase = true))  templst.add(it)
            }
            _tempApts.postValue(templst)
        }
        else{
            _tempApts.postValue(_apts.value?.toMutableList())
        }
    }

}