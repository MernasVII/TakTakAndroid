package tn.esprit.taktakandroid.uis.common.bid

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.models.entities.Bid
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.models.requests.MakeBidRequest
import tn.esprit.taktakandroid.models.responses.MessageResponse
import tn.esprit.taktakandroid.models.responses.ReceivedBidsResponse
import tn.esprit.taktakandroid.models.responses.SentBidsResponse
import tn.esprit.taktakandroid.repositories.BidRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class BidViewModel(private val bidRepository: BidRepository
) : ViewModel() {
    private val TAG:String="BidViewModel"

    val makeBidRes: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()

    private val _getSentBidsResult= MutableLiveData<Resource<SentBidsResponse>>()
    val sentBidsRes: LiveData<Resource<SentBidsResponse>>
        get() = _getSentBidsResult

    private val _tempSentBids = MutableLiveData<MutableList<Bid>>()
    val sentTempBids: LiveData<MutableList<Bid>> = _tempSentBids

    private val _sentBids = MutableLiveData<List<Bid>>()
    val sentBids: LiveData<List<Bid>> = _sentBids

    private val _getReceivedBidsResult= MutableLiveData<Resource<ReceivedBidsResponse>>()
    val receivedBidsRes: LiveData<Resource<ReceivedBidsResponse>>
        get() = _getReceivedBidsResult

    private val _tempReceivedBids = MutableLiveData<MutableList<Bid>>()
    val receivedTempBids: LiveData<MutableList<Bid>> = _tempReceivedBids

    private val _receivedBids = MutableLiveData<List<Bid>>()
    val receivedBids: LiveData<List<Bid>> = _receivedBids

    init {
        _tempSentBids.value = mutableListOf()
        _sentBids.value = listOf()
        _tempReceivedBids.value= mutableListOf()
        _receivedBids.value= listOf()
    }

    private val handler = CoroutineExceptionHandler { _, _ ->
        makeBidRes.postValue(Resource.Error("Server connection failed!"))
    }

    fun getSentBidsList() = viewModelScope.launch {
        try {
            _sentBids.postValue(listOf())
            _getSentBidsResult.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            val response= bidRepository.getSentBids("Bearer $token")
            _getSentBidsResult.postValue(handleSentBidsResponse(response))
        } catch (exception: Exception) {
            _getSentBidsResult.postValue(Resource.Error("Server connection failed!"))
        }
    }

    fun getReceivedBidsList(idBodyRequest: IdBodyRequest) = viewModelScope.launch {
            _receivedBids.postValue(listOf())
            _getReceivedBidsResult.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            val response= bidRepository.getReceivedBids("Bearer $token",idBodyRequest)
            _getReceivedBidsResult.postValue(handleReceivedBidsResponse(response))

    }

    fun makeBid(makeBidRequest: MakeBidRequest) = viewModelScope.launch {
        try {
            makeBidRes.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            viewModelScope.launch(handler) {
                val response = bidRepository.makeBid("Bearer $token", makeBidRequest)
                makeBidRes.postValue(handleMakeBidResponse(response))
            }
        } catch (e: Exception) {
            makeBidRes.postValue(Resource.Error("Server connection failed!"))
        }
    }

    private fun handleMakeBidResponse(response: Response<MessageResponse>): Resource<MessageResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))
    }

    private fun handleSentBidsResponse(response: Response<SentBidsResponse>): Resource<SentBidsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                _sentBids.postValue(resultResponse.bids)
                return Resource.Success(resultResponse)
            }
        }else{
            _sentBids.postValue(listOf())
        }
        return Resource.Error(response.message())
    }

    private fun handleReceivedBidsResponse(response: Response<ReceivedBidsResponse>): Resource<ReceivedBidsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                _receivedBids.postValue(resultResponse.receivedBids)
                return Resource.Success(resultResponse)
            }
        }else{
            _receivedBids.postValue(listOf())
        }
        return Resource.Error(response.message())
    }

    fun filterSent(filtredVal:String){
        _tempSentBids.value?.clear()
        val templst= mutableListOf<Bid>()
        if(!_sentBids.value.isNullOrEmpty() && !filtredVal.isNullOrEmpty()){
            _sentBids.value!!.forEach {
                var user=it.request.customer
                if(user.firstname!!.contains(filtredVal, ignoreCase = true) || user.lastname!!.contains(filtredVal, ignoreCase = true) || it.request.tos.contains(filtredVal, ignoreCase = true))  templst.add(it)
            }
            _tempSentBids.postValue(templst)
        }
        else{
            _tempSentBids.postValue(_sentBids.value?.toMutableList())
        }
    }

    fun filterReceived(filtredVal:String){
        _tempReceivedBids.value?.clear()
        val templst= mutableListOf<Bid>()
        if(!_receivedBids.value.isNullOrEmpty() && !filtredVal.isNullOrEmpty()){
            _receivedBids.value!!.forEach {
                var user=it.sp
                if(user.firstname!!.contains(filtredVal, ignoreCase = true) || user.lastname!!.contains(filtredVal, ignoreCase = true) || user.speciality!!.contains(filtredVal, ignoreCase = true))  templst.add(it)
            }
            _tempReceivedBids.postValue(templst)
        }
        else{
            _tempReceivedBids.postValue(_receivedBids.value?.toMutableList())
        }
    }
}