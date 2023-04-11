package tn.esprit.taktakandroid.uis.common.apts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Response
import tn.esprit.taktakandroid.models.responses.AptsResponse
import tn.esprit.taktakandroid.repositories.AptRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class AptsViewModel  (private val aptRepository: AptRepository
) : ViewModel() {
    private val TAG:String="AptsViewModel"

    var cin:String?=""
    val aptsResult: MutableLiveData<Resource<AptsResponse>> = MutableLiveData()

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

    private fun handleAptResponse(response: Response<AptsResponse>): Resource<AptsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }


}