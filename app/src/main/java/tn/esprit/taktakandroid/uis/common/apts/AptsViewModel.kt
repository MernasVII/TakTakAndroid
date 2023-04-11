package tn.esprit.taktakandroid.uis.common.apts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.models.MessageResponse
import tn.esprit.taktakandroid.models.requests.AcceptAptRequest
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.models.requests.PostponeAptRequest
import tn.esprit.taktakandroid.models.requests.UpdateAptStateRequest
import tn.esprit.taktakandroid.models.responses.AptsResponse
import tn.esprit.taktakandroid.models.responses.TimeLeftResponse
import tn.esprit.taktakandroid.repositories.AptRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class AptsViewModel  (private val aptRepository: AptRepository
) : ViewModel() {
    private val TAG:String="AptsViewModel"

    var cin:String?=""
    val aptsResult: MutableLiveData<Resource<AptsResponse>> = MutableLiveData()
    val putAptRes: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()
    val timeLeftAptRes: MutableLiveData<Resource<TimeLeftResponse>> = MutableLiveData()

    init {
        getAptsList()
    }

    private fun getAptsList() = viewModelScope.launch {
        try {
            aptsResult.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            cin = AppDataStore.readString(Constants.CIN)
            val response : Response<AptsResponse> = if(cin.isNullOrEmpty()){
                aptRepository.getRequestedAcceptedApts("Bearer $token")
            }else{
                aptRepository.getReceivedAcceptedApts("Bearer $token")
            }
            aptsResult.postValue(handleAptResponse(response))
        } catch (exception: Exception) {
            aptsResult.postValue(Resource.Error("Server connection failed!"))
        }
    }

    fun cancelApt(idBodyRequest: IdBodyRequest) = viewModelScope.launch {
            try {
                putAptRes.postValue(Resource.Loading())
                val token = AppDataStore.readString(Constants.AUTH_TOKEN)
                viewModelScope.launch(handler) {
                    val response = aptRepository.cancelApt("Bearer $token", idBodyRequest)
                    putAptRes.postValue(handlePutAptResponse(response))
                }

            } catch (e: Exception) {
                putAptRes.postValue(Resource.Error("Server connection failed!"))
            }
    }

    fun postponeApt(postponeAptRequest: PostponeAptRequest) = viewModelScope.launch {
        try {
            putAptRes.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            viewModelScope.launch(handler) {
                val response = aptRepository.postponeApt("Bearer $token", postponeAptRequest)
                putAptRes.postValue(handlePutAptResponse(response))
            }

        } catch (e: Exception) {
            putAptRes.postValue(Resource.Error("Server connection failed!"))
        }
    }

    fun acceptApt(acceptAptRequest: AcceptAptRequest) = viewModelScope.launch {
        try {
            putAptRes.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            viewModelScope.launch(handler) {
                val response = aptRepository.acceptApt("Bearer $token", acceptAptRequest)
                putAptRes.postValue(handleAcceptAptResponse(response))
            }

        } catch (e: Exception) {
            putAptRes.postValue(Resource.Error("Server connection failed!"))
        }
    }

    fun declineApt(idBodyRequest: IdBodyRequest) = viewModelScope.launch {
        try {
            putAptRes.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            viewModelScope.launch(handler) {
                val response = aptRepository.declineApt("Bearer $token", idBodyRequest)
                putAptRes.postValue(handlePutAptResponse(response))
            }

        } catch (e: Exception) {
            putAptRes.postValue(Resource.Error("Server connection failed!"))
        }
    }

    fun updateAptState(updateAptStateRequest: UpdateAptStateRequest) = viewModelScope.launch {
        try {
            putAptRes.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            viewModelScope.launch(handler) {
                val response = aptRepository.updateAptState("Bearer $token", updateAptStateRequest)
                putAptRes.postValue(handlePutAptResponse(response))
            }

        } catch (e: Exception) {
            putAptRes.postValue(Resource.Error("Server connection failed!"))
        }
    }

    fun getTimeLeftToApt(idBodyRequest: IdBodyRequest) = viewModelScope.launch {
        timeLeftAptRes.postValue(Resource.Loading())
        val token = AppDataStore.readString(Constants.AUTH_TOKEN)
        val response = aptRepository.getTimeLeftToApt("Bearer $token",idBodyRequest)
        timeLeftAptRes.postValue(handleTimeLeftAptResponse(response))

    }

    private fun handleAptResponse(response: Response<AptsResponse>): Resource<AptsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private val handler = CoroutineExceptionHandler { _, _ ->
        putAptRes.postValue(Resource.Error("Server connection failed!"))
    }

    private fun handleAcceptAptResponse(response: Response<MessageResponse>): Resource<MessageResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))
    }

    private fun handlePutAptResponse(response: Response<MessageResponse>): Resource<MessageResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))
    }

    private fun handleTimeLeftAptResponse(response: Response<TimeLeftResponse>): Resource<TimeLeftResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

}