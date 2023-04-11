package tn.esprit.taktakandroid.uis.common.aptsarchived

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

class ArchivedAptsViewModel  (private val aptRepository: AptRepository
) : ViewModel() {
    private val TAG:String="ArchivedAptsViewModel"

    var cin:String?=""
    val archivedAptsResult: MutableLiveData<Resource<AptsResponse>> = MutableLiveData()

    init {
        getArchivedAptsList()
    }

    private fun getArchivedAptsList() = viewModelScope.launch {
        try {
            archivedAptsResult.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            cin = AppDataStore.readString(Constants.CIN)
            val response : Response<AptsResponse> = if(cin.isNullOrEmpty()){
                aptRepository.getRequestedArchivedApts("Bearer $token")
            }else{
                aptRepository.getReceivedArchivedApts("Bearer $token")
            }
            archivedAptsResult.postValue(handleAptResponse(response))
        } catch (exception: Exception) {
            archivedAptsResult.postValue(Resource.Error("Server connection failed!"))
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