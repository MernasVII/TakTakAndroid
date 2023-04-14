package tn.esprit.taktakandroid.uis.customer.spslist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.models.responses.SPsResponse
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class SPsViewModel(private val userRepository: UserRepository
) : ViewModel() {
    private val TAG:String="SPsViewModel"

    private val _getSPsResult= MutableLiveData<Resource<SPsResponse>>()
    val spsRes: LiveData<Resource<SPsResponse>>
        get() = _getSPsResult

    private val _tempSPs = MutableLiveData<MutableList<User>>()
    val tempSPs: LiveData<MutableList<User>> = _tempSPs

    private val _sps = MutableLiveData<List<User>>()
    val sps: LiveData<List<User>> = _sps

    //val spsResult: MutableLiveData<Resource<SPsResponse>> = MutableLiveData()


    init {
        _tempSPs.value = mutableListOf()
        _sps.value = listOf()
    }

    fun getSPsList() = viewModelScope.launch {
        try {
            _getSPsResult.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            val response = userRepository.getSPsList("Bearer $token")
            _getSPsResult.postValue(handleSPsResponse(response))
        } catch (exception: Exception) {
            _getSPsResult.postValue(Resource.Error("Server connection failed!"))
        }
    }

    private fun handleSPsResponse(response: Response<SPsResponse>): Resource<SPsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                _sps.postValue(resultResponse.users)
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun filter(filtredVal:String){
        _tempSPs.value?.clear()
        val templst= mutableListOf<User>()
        if(!_sps.value.isNullOrEmpty() && !filtredVal.isNullOrEmpty()){
            _sps.value!!.forEach {
                if(it.firstname!!.contains(filtredVal, ignoreCase = true) || it.lastname!!.contains(filtredVal, ignoreCase = true)  || it.speciality!!.contains(filtredVal, ignoreCase = true))  templst.add(it)
            }
            _tempSPs.postValue(templst)
        }
        else{
            _tempSPs.postValue(_sps.value?.toMutableList())
        }
    }

}