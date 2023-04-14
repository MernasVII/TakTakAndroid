package tn.esprit.taktakandroid.uis.common.notifs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.models.entities.Notification
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.models.responses.MessageResponse
import tn.esprit.taktakandroid.models.responses.NotifsResponse
import tn.esprit.taktakandroid.repositories.NotifRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class NotifsViewModel (private val notifRepository: NotifRepository
) : ViewModel() {
    private val TAG:String="NotifsViewModel"

    private val _getNotifsResult= MutableLiveData<Resource<NotifsResponse>>()
    val notifsRes: LiveData<Resource<NotifsResponse>>
        get() = _getNotifsResult

    private val _tempNotifs = MutableLiveData<MutableList<Notification>>()
    val tempNotifs: LiveData<MutableList<Notification>> = _tempNotifs

    private val _notifs = MutableLiveData<List<Notification>>()
    val notifs: LiveData<List<Notification>> = _notifs

    //val notifsRes: MutableLiveData<Resource<NotifsResponse>> = MutableLiveData()
    val readNotifRes: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()

    init {
        _tempNotifs.value = mutableListOf()
        _notifs.value = listOf()
    }

    fun getNotifsList() = viewModelScope.launch {
        try {
            _getNotifsResult.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            val response = notifRepository.getNotifsList("Bearer $token")
            _getNotifsResult.postValue(handleNotifsResponse(response))
        } catch (exception: Exception) {
            _getNotifsResult.postValue(Resource.Error("Server connection failed!"))
        }
    }

    private fun handleNotifsResponse(response: Response<NotifsResponse>): Resource<NotifsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                _notifs.postValue(resultResponse.notifs)
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private val handler = CoroutineExceptionHandler { _, _ ->
        readNotifRes.postValue(Resource.Error("Server connection failed!"))
    }

    fun markRead(idBodyRequest: IdBodyRequest) = viewModelScope.launch {
        try {
            readNotifRes.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            viewModelScope.launch(handler) {
                val response = notifRepository.markRead("Bearer $token", idBodyRequest)
                readNotifRes.postValue(handleReadNotifResponse(response))
            }

        } catch (e: Exception) {
            readNotifRes.postValue(Resource.Error("Server connection failed!"))
        }
    }

    private fun handleReadNotifResponse(response: Response<MessageResponse>): Resource<MessageResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))
    }

    fun filter(filtredVal:String){
        _tempNotifs.value!!.clear()
        val templst= mutableListOf<Notification>()
        if(!_notifs.value.isNullOrEmpty() && !filtredVal.isNullOrEmpty()){
            _notifs.value!!.forEach {
                if(it.content.contains(filtredVal, ignoreCase = true))  templst.add(it)
            }
            _tempNotifs.postValue(templst)
        }
        else{
            _tempNotifs.postValue(_notifs.value!!.toMutableList())
        }
    }


}