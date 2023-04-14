package tn.esprit.taktakandroid.uis.common.aptsarchived

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response
import tn.esprit.taktakandroid.models.entities.Appointment
import tn.esprit.taktakandroid.models.entities.User
import tn.esprit.taktakandroid.models.responses.AptsResponse
import tn.esprit.taktakandroid.repositories.AptRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class ArchivedAptsViewModel  (private val aptRepository: AptRepository
) : ViewModel() {
    private val TAG:String="ArchivedAptsViewModel"

    var cin:String?=""
    //val archivedAptsResult: MutableLiveData<Resource<AptsResponse>> = MutableLiveData()

    private val _getAptsResult= MutableLiveData<Resource<AptsResponse>>()
    val aptsRes: LiveData<Resource<AptsResponse>>
        get() = _getAptsResult

    private val _tempApts = MutableLiveData<MutableList<Appointment>>()
    val tempApts: LiveData<MutableList<Appointment>> = _tempApts

    private val _apts = MutableLiveData<List<Appointment>>()
    val apts: LiveData<List<Appointment>> = _apts

    init {
        _tempApts.value = mutableListOf()
        _apts.value = listOf()
    }

    fun getArchivedAptsList() = viewModelScope.launch {
        try {
            _getAptsResult.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            cin = AppDataStore.readString(Constants.CIN)
            val response : Response<AptsResponse> = if(cin.isNullOrEmpty()){
                aptRepository.getRequestedArchivedApts("Bearer $token")
            }else{
                aptRepository.getReceivedArchivedApts("Bearer $token")
            }
            _getAptsResult.postValue(handleAptResponse(response))
        } catch (exception: Exception) {
            _getAptsResult.postValue(Resource.Error("Server connection failed!"))
        }
    }

    private fun handleAptResponse(response: Response<AptsResponse>): Resource<AptsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                _apts.postValue(resultResponse.appointments)
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    fun filter(filtredVal:String,cin:String){
        _tempApts.value?.clear()
        val templst= mutableListOf<Appointment>()
        if(!_apts.value.isNullOrEmpty() && !filtredVal.isNullOrEmpty()){
            _apts.value!!.forEach {
                var user: User
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