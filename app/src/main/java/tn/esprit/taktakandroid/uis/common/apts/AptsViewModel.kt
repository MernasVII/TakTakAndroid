package tn.esprit.taktakandroid.uis.common.apts

<<<<<<< HEAD
import android.app.Application
import android.content.Context
import androidx.lifecycle.*
=======
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
>>>>>>> a94bf974a85d70fb53b8dbae25c21981f14c240c
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.models.entities.Appointment
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.models.requests.*
import tn.esprit.taktakandroid.models.responses.AptsResponse
import tn.esprit.taktakandroid.models.responses.GetAptResponse
import tn.esprit.taktakandroid.models.responses.MessageResponse
import tn.esprit.taktakandroid.models.responses.UserProfileResponse
import tn.esprit.taktakandroid.repositories.AptRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource
import tn.esprit.taktakandroid.utils.SocketService

class AptsViewModel  (private val aptRepository: AptRepository,private val app:Application
) : AndroidViewModel(application = app) {
    private val TAG:String="AptsViewModel"

    var cin:String?=""

    private val _getAptsResult= MutableLiveData<Resource<AptsResponse>>()
    val aptsRes: LiveData<Resource<AptsResponse>>
        get() = _getAptsResult

    val getAptRes: MutableLiveData<Resource<GetAptResponse>> = MutableLiveData()
    val findAptRes: MutableLiveData<Resource<GetAptResponse>> = MutableLiveData()


    private val _tempApts = MutableLiveData<MutableList<Appointment>>()
    val tempApts: LiveData<MutableList<Appointment>> = _tempApts

    private val _apts = MutableLiveData<List<Appointment>>()
    val apts: LiveData<List<Appointment>> = _apts

    //val aptsResult: MutableLiveData<Resource<AptsResponse>> = MutableLiveData()
    val cancelAptRes: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()
    val archiveAptRes: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()
    val postponeAptRes: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()
    val updateStateAptRes: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()
    val rateAptRes: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()
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
            _getAptsResult.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
        }
    }

    fun cancelApt(idBodyRequest: IdBodyRequest,spID:String) = viewModelScope.launch {
            try {
                cancelAptRes.postValue(Resource.Loading())
                val token = AppDataStore.readString(Constants.AUTH_TOKEN)
                viewModelScope.launch(handler) {
                    val response = aptRepository.cancelApt("Bearer $token", idBodyRequest)
                    cancelAptRes.postValue(handleAptCancelResponse(response,spID))
                }
            } catch (e: Exception) {
                cancelAptRes.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
            }
    }

    fun rateApt(rateBodyRequest: RateBodyRequest) = viewModelScope.launch {
        try {
            rateAptRes.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            viewModelScope.launch(handler) {
                val response = aptRepository.rateApt("Bearer $token", rateBodyRequest)
                rateAptRes.postValue(handleRateAptResponse(response))
            }
        } catch (e: Exception) {
            rateAptRes.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
        }
    }

    fun postponeApt(postponeAptRequest: PostponeAptRequest,customerID:String) = viewModelScope.launch {
        try {
            postponeAptRes.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            viewModelScope.launch(handler) {
                val response = aptRepository.postponeApt("Bearer $token", postponeAptRequest)
                postponeAptRes.postValue(handleAptPostponeResponse(response,customerID))
            }

        } catch (e: Exception) {
            postponeAptRes.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
        }
    }

    fun archiveApt(idBodyRequest: IdBodyRequest) = viewModelScope.launch {
        try {
            archiveAptRes.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            viewModelScope.launch(handler) {
                val response = aptRepository.cancelApt("Bearer $token", idBodyRequest)
                archiveAptRes.postValue(handleAptArchiveResponse(response))
            }
        } catch (e: Exception) {
            archiveAptRes.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
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
            updateStateAptRes.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
        }
    }

    fun getApt(idBodyRequest: IdBodyRequest) = viewModelScope.launch {
        try {
            getAptRes.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            val response = aptRepository.getApt("Bearer $token",idBodyRequest)
            getAptRes.postValue(handleGetAptResponse(response))
        } catch (exception: Exception) {
            getAptRes.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
        }
    }

    fun findApt(findAptRequest: FindAptRequest) = viewModelScope.launch {
        try {
            findAptRes.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            val response = aptRepository.findApt("Bearer $token",findAptRequest)
            findAptRes.postValue(handleFindAptResponse(response))
        } catch (exception: Exception) {
            findAptRes.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
        }
    }


    private fun handleGetAptResponse(response: Response<GetAptResponse>): Resource<GetAptResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                Log.d(TAG, "handleGetAptResponse: ${resultResponse.apt.rate}")
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleFindAptResponse(response: Response<GetAptResponse>): Resource<GetAptResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
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
        cancelAptRes.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
        postponeAptRes.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
        updateStateAptRes.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
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

    private fun handleAptCancelResponse(response: Response<MessageResponse>,spID:String): Resource<MessageResponse> {
        if (response.isSuccessful) {
            viewModelScope.launch {
                val currUserID = AppDataStore.readString(Constants.USER_ID)
                val msg = "$spID/ canceled an appointment!/$currUserID"
                SocketService.sendMessage(msg)
            }
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))
    }

    private fun handleAptArchiveResponse(response: Response<MessageResponse>): Resource<MessageResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))
    }

    private fun handleAptPostponeResponse(response: Response<MessageResponse>,customerID:String): Resource<MessageResponse> {
        if (response.isSuccessful) {
            viewModelScope.launch {
                val currUserID = AppDataStore.readString(Constants.USER_ID)
                val msg = "$customerID/ postponed your appointment!/$currUserID"
                SocketService.sendMessage(msg)
            }
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))
    }

    private fun handleRateAptResponse(response: Response<MessageResponse>): Resource<MessageResponse> {
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