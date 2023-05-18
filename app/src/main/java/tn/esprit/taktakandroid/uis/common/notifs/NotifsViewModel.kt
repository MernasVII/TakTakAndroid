package tn.esprit.taktakandroid.uis.common.notifs

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.models.entities.Notification
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.models.responses.CountNotifsResponse
import tn.esprit.taktakandroid.models.responses.MessageResponse
import tn.esprit.taktakandroid.models.responses.NotifsResponse
import tn.esprit.taktakandroid.repositories.NotifRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class NotifsViewModel (private val notifRepository: NotifRepository,private val app: Application
) : AndroidViewModel(application = app) {
    private val TAG:String="NotifsViewModel"
    private val _countNotifResult = MutableLiveData<Resource<CountNotifsResponse>>()
    val countNotifResult: LiveData<Resource<CountNotifsResponse>>
        get() = _countNotifResult
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
            _notifs.postValue(listOf())
            _getNotifsResult.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            val response = notifRepository.getNotifsList("Bearer $token")
            _getNotifsResult.postValue(handleNotifsResponse(response))

    }

    private fun handleNotifsResponse(response: Response<NotifsResponse>): Resource<NotifsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                _notifs.postValue(resultResponse.notifs)
                return Resource.Success(resultResponse)
            }
        }else{
            _notifs.postValue(listOf())
        }
        return Resource.Error(response.message())
    }

    private val handler = CoroutineExceptionHandler { _, _ ->
        readNotifRes.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
    }
    private val countHandler = CoroutineExceptionHandler { _, _ ->
        _countNotifResult.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))

    }
     fun countMyNotif() {
        viewModelScope.launch (countHandler){
            try {
                _countNotifResult.postValue(Resource.Loading())
                val token = AppDataStore.readString(Constants.AUTH_TOKEN)
                val response = notifRepository.countNotifs("Bearer $token")
                _countNotifResult.postValue(handleResponse(response))
            } catch (exception: Exception) {
                _countNotifResult.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
            }
        }
    }
    private fun handleResponse(response: Response<CountNotifsResponse>): Resource<CountNotifsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                Log.d(TAG, "${resultResponse.nbNotif} ")
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))
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
            readNotifRes.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
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
        _tempNotifs.value?.clear()
        val templst= mutableListOf<Notification>()
        if(!_notifs.value.isNullOrEmpty() && !filtredVal.isNullOrEmpty()){
            _notifs.value!!.forEach {
                if(it.content.contains(filtredVal, ignoreCase = true))  templst.add(it)
            }
            _tempNotifs.postValue(templst)
        }
        else{
            _tempNotifs.postValue(_notifs.value?.toMutableList())
        }
    }


}