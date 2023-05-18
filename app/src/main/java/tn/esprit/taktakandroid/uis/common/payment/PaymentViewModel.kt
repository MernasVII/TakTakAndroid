package tn.esprit.taktakandroid.uis.common.payment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.R
import tn.esprit.taktakandroid.models.entities.Appointment
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.models.requests.InitPaymentRequest
import tn.esprit.taktakandroid.models.requests.SendLinkRequest
import tn.esprit.taktakandroid.models.responses.InitPaymentResponse
import tn.esprit.taktakandroid.models.responses.MessageResponse
import tn.esprit.taktakandroid.models.responses.PaymentStatusResponse
import tn.esprit.taktakandroid.repositories.PaymentRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class PaymentViewModel(
    private val repository: PaymentRepository,
    private val appointment: Appointment?,
    private val app: Application,
) : AndroidViewModel(application = app
) {
    val initRes: MutableLiveData<Resource<InitPaymentResponse>> = MutableLiveData()
    val statusRes: MutableLiveData<Resource<PaymentStatusResponse>> = MutableLiveData()
    val sendLinkRes: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()

    init {
        initPayment()
    }

    private val handler = CoroutineExceptionHandler { _, _ ->
        initRes.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
        statusRes.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
        sendLinkRes.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
    }

    fun initPayment() = viewModelScope.launch {
        try {
            initRes.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            val response = repository.initPayment(
                "Bearer $token", InitPaymentRequest(
                    appointment!!.desc,
                    appointment.customer.email!!,
                    appointment.customer.firstname!!,
                    appointment.customer.lastname!!,
                    appointment.price!!,
                )
            )
            initRes.postValue(handleInitResponse(response))
        } catch (e: Exception) {
            initRes.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
        }
    }

    fun paymentStatus(idBodyRequest: IdBodyRequest) = viewModelScope.launch {
        try {
            statusRes.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            val response = repository.paymentStatus(
                "Bearer $token", idBodyRequest
            )
            statusRes.postValue(handleStatusResponse(response))
        } catch (e: Exception) {
            statusRes.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))
        }
    }

    fun sendLink(email: String, link: String) = viewModelScope.launch {
        try {
            sendLinkRes.postValue(Resource.Loading())
            val token = AppDataStore.readString(Constants.AUTH_TOKEN)
            val response = repository.sendLink(
                "Bearer $token", SendLinkRequest(
                    email, link
                )
            )
            sendLinkRes.postValue(handleSendLinkResponse(response))
        } catch (e: Exception) {
            sendLinkRes.postValue(Resource.Error(app.getString(R.string.server_connection_failed)))

        }
    }

    private fun handleSendLinkResponse(response: Response<MessageResponse>): Resource<MessageResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))
    }

    private fun handleInitResponse(response: Response<InitPaymentResponse>): Resource<InitPaymentResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))
    }

    private fun handleStatusResponse(response: Response<PaymentStatusResponse>): Resource<PaymentStatusResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))
    }
}