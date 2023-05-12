package tn.esprit.taktakandroid.uis.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.models.requests.FindAptRequest
import tn.esprit.taktakandroid.models.responses.AptsResponse
import tn.esprit.taktakandroid.models.responses.CountNotifsResponse
import tn.esprit.taktakandroid.models.responses.UserArchivedReqResponse
import tn.esprit.taktakandroid.repositories.NotifRepository
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class HomeViewModel(
    private val notifRepository: NotifRepository
) : ViewModel() {






}