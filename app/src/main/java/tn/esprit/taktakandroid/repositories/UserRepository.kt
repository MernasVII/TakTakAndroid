package tn.esprit.taktakandroid.repositories

import retrofit2.Response
import tn.esprit.taktakandroid.api.RetrofitInstance
import tn.esprit.taktakandroid.models.login.LoginRequest
import tn.esprit.taktakandroid.models.sendOtp.SendOtpRequest
import tn.esprit.taktakandroid.models.sendOtp.SendOtpResponse
import java.util.*

class UserRepository {

    suspend fun login(request: LoginRequest) = RetrofitInstance.userApi.login(request)

    suspend fun sendOtp( sendOtpRequest: SendOtpRequest) : Response<SendOtpResponse>{
        return RetrofitInstance.userApi.sendOtp(sendOtpRequest)
    }

    fun generateOTP(): String {
        val random = Random()
        val otp = random.nextInt(10000)
        return String.format("%04d", otp)
    }
}