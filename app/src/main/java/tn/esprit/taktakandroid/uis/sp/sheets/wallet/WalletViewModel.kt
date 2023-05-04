package tn.esprit.taktakandroid.uis.sp.sheets.wallet

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import tn.esprit.taktakandroid.models.requests.CheckPwdRequest
import tn.esprit.taktakandroid.models.responses.MessageResponse
import tn.esprit.taktakandroid.models.requests.UpdateWorkDescRequest
import tn.esprit.taktakandroid.models.requests.WalletReqResp
import tn.esprit.taktakandroid.repositories.UserRepository
import tn.esprit.taktakandroid.repositories.WalletRepository
import tn.esprit.taktakandroid.utils.AppDataStore
import tn.esprit.taktakandroid.utils.Constants
import tn.esprit.taktakandroid.utils.Resource

class WalletViewModel(private val repository: UserRepository,private val walletRepository: WalletRepository) : ViewModel(
) {
    val checkPWDResult: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()
    val getMyBalanceResult: MutableLiveData<Resource<WalletReqResp>> = MutableLiveData()
    val withdrawMoneyResult: MutableLiveData<Resource<MessageResponse>> = MutableLiveData()

    private val _currentBalance = MutableLiveData<String>()
    val currentBalance: LiveData<String>
        get() = _currentBalance

    private val _pwd = MutableLiveData<String>()
    val pwd: LiveData<String>
        get() = _pwd

    private val _pwdError = MutableLiveData<String>()
    val pwdError: LiveData<String>
        get() = _pwdError


    fun setPassword(pwd: String) {
        _pwd.value = pwd
    }

    fun removePwdError() {
        _pwdError.value = ""
    }


    private val handler = CoroutineExceptionHandler { _, _ ->
        checkPWDResult.postValue(Resource.Error("Server connection failed!"))
    }
    private val getMyBalanceResultHandler = CoroutineExceptionHandler { _, _ ->
        getMyBalanceResult.postValue(Resource.Error("Server connection failed!"))
    }
    private val withdrawMoneyResultHandler = CoroutineExceptionHandler { _, _ ->
        withdrawMoneyResult.postValue(Resource.Error("Server connection failed!"))
    }

    fun verifyPassword() = viewModelScope.launch {
        val password = _pwd.value

        if (isPwdValid(password)) {
            try {
                checkPWDResult.postValue(Resource.Loading())
                val token = AppDataStore.readString(Constants.AUTH_TOKEN)
                viewModelScope.launch(handler) {
                    val response = repository.checkPassword(
                        "Bearer $token", CheckPwdRequest(
                            password!!
                        )
                    )
                    checkPWDResult.postValue(handleCheckPwdResponse(response))
                }

            } catch (e: Exception) {
                checkPWDResult.postValue(Resource.Error("Server connection failed!"))
            }

        }
    }

    fun getCurrentBalance() {
        viewModelScope.launch {
                try {
                    getMyBalanceResult.postValue(Resource.Loading())
                    val token = AppDataStore.readString(Constants.AUTH_TOKEN)
                    viewModelScope.launch(getMyBalanceResultHandler) {
                        val response = walletRepository.getBalance(
                            "Bearer $token"
                        )
                        getMyBalanceResult.postValue(handleGetBalanceResponse(response))
                    }

                } catch (e: Exception) {
                    getMyBalanceResult.postValue(Resource.Error("Server connection failed!"))
                }
        }
    }

    fun withdrawMoney() {
        viewModelScope.launch {
            try {
                withdrawMoneyResult.postValue(Resource.Loading())
                val token = AppDataStore.readString(Constants.AUTH_TOKEN)
                viewModelScope.launch(withdrawMoneyResultHandler) {
                    val response = walletRepository.withdrawMoney(
                        "Bearer $token"
                    )
                    withdrawMoneyResult.postValue(handleWithdrawMoneyResponse(response))
                }

            } catch (e: Exception) {
                withdrawMoneyResult.postValue(Resource.Error("Server connection failed!"))
            }
        }
    }
    private fun handleGetBalanceResponse(response: Response<WalletReqResp>): Resource<WalletReqResp> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))
    }
    private fun handleWithdrawMoneyResponse(response: Response<MessageResponse>): Resource<MessageResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))
    }

    private fun handleCheckPwdResponse(response: Response<MessageResponse>): Resource<MessageResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        val errorBody = JSONObject(response.errorBody()!!.string())
        return Resource.Error(errorBody.getString("message"))
    }


    private fun isPwdValid(pwd: String?): Boolean {
        if (pwd.isNullOrEmpty()) {
            _pwdError.postValue("Password cannot be empty!")
            return false
        }
        return true
    }

}