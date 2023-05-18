package tn.esprit.taktakandroid.uis.sp.sheets.updatework

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.models.responses.MessageResponse
import tn.esprit.taktakandroid.models.requests.UpdateWorkDescRequest
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class UpdateWorkDescriptionViewModel (private val repository: UserRepository,private val application: Application) : AndroidViewModel(application
) {
    val updateWorkDescRes: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()

    private val _speciality = MutableLiveData<String>()
    val speciality: LiveData<String>
        get() = _speciality

    private val _specialityError = MutableLiveData<String>()
    val specialityError: LiveData<String>
        get() = _specialityError

    private val _tos = MutableLiveData<ArrayList<String>>()
    val tos: LiveData<ArrayList<String>>
        get() = _tos

    private val _tosError = MutableLiveData<String>()
    val tosError: LiveData<String>
        get() = _tosError

    private val _workDays = MutableLiveData<ArrayList<String>>()
    val workDays: LiveData<ArrayList<String>>
        get() = _workDays

    private val _workDaysError = MutableLiveData<String>()
    val workDaysError: LiveData<String>
        get() = _workDaysError



    init {
        _tos.value = arrayListOf()
        _workDays.value = arrayListOf()
    }

    fun setSpeciality(spec: String) {
        _speciality.value = spec
    }

    fun addTos(tos: String) {
        _tos.value?.add(tos)
    }

    fun deleteTos(tos: String) {
        _tos.value?.remove(tos)
    }

    fun addDay(day: String) {
        _workDays.value?.add(day)
    }

    fun deleteDay(day: String) {
        _workDays.value?.remove(day)
    }

    fun removeSpecialityError() {
        _specialityError.value = ""
    }

    private val handler = CoroutineExceptionHandler { _, _ ->
        updateWorkDescRes.postValue(Resource.Error(application.getString(R.string.server_connection_failed)))
    }

    fun updateWorkDesc() = viewModelScope.launch {
        val speciality = _speciality.value
        val tos = _tos.value
        val workDays = _workDays.value
        if (fieldsValidation(speciality, tos, workDays)) {
            try {
                updateWorkDescRes.postValue(Resource.Loading())
                val token = AppDataStore.readString(Constants.AUTH_TOKEN)
                viewModelScope.launch(handler) {
                    val response = repository.updateWorkDesc("Bearer $token", UpdateWorkDescRequest(
                        speciality!!,
                        tos!!,
                        workDays!!
                    )
                    )
                    updateWorkDescRes.postValue(handleResponse(response))
                }

            } catch (e: Exception) {
                updateWorkDescRes.postValue(Resource.Error(application.getString(R.string.server_connection_failed)))
            }

        }
    }


    private fun handleResponse(response: Response<MessageResponse>): Resource<MessageResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))
    }

    private fun fieldsValidation(
        speciality: String?, tos: ArrayList<String>?, workDays: ArrayList<String>?
    ): Boolean {
        val isSpecialityValid = isSpecialityValid(speciality)
        val isTosValid = isTosValid(tos)
        val isWorkDaysValid = isWorkDaysValid(workDays)
        return isSpecialityValid && isTosValid && isWorkDaysValid
    }

    private fun isSpecialityValid(speciality: String?): Boolean {
        if (speciality.isNullOrEmpty()) {
            _specialityError.postValue(application.getString(R.string.specilality_cant_be_empty))
            return false
        }
        return true
    }
    private fun isTosValid(tos: ArrayList<String>?): Boolean {
        if (tos.isNullOrEmpty()) {
            _tosError.postValue(application.getString(R.string.tos_cant_be_empty))
            return false
        }
        return true
    }

    private fun isWorkDaysValid(workDays: ArrayList<String>?): Boolean {
        if (workDays.isNullOrEmpty()) {
            _workDaysError.postValue(application.getString(R.string.work_days_cannot_be_empty))
            return false
        }
        return true
    }
}