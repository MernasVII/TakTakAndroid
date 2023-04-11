package tn.esprit.taktakandroid.uis.common.notifs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.models.MessageResponse
import tn.esprit.taktakandroid.models.entities.Notification
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.models.responses.NotifsResponse
import tn.esprit.taktakandroid.repositories.NotifRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class NotifsViewModel (private val notifRepository: NotifRepository
) : ViewModel() {
    private val TAG:String="NotifsViewModel"

    val notifsRes: MutableLiveData<Resource<NotifsResponse>> = MutableLiveData()
    private val _filteredItems = MutableLiveData<List<Notification>>()
    val filteredItems: LiveData<List<Notification>> = _filteredItems
    val readNotifRes: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()

    init {
        getNotifsList()
    }

    private fun getNotifsList(query: String = "") = viewModelScope.launch {
        try {
            notifsRes.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            val response = notifRepository.getNotifsList("Bearer $token")
            notifsRes.postValue(handleNotifsResponse(response))
            filterItems(query)
        } catch (exception: Exception) {
            notifsRes.postValue(Resource.Error("Server connection failed!"))
        }
    }

    private fun handleNotifsResponse(response: Response<NotifsResponse>): Resource<NotifsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun filterItems(query: String) {
        val filteredList = notifsRes.value?.data?.notifs?.filter {
            val content=it.content
            content.contains(query, ignoreCase = true)
        }
        _filteredItems.postValue(filteredList)
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


}