package tn.esprit.taktakandroid.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import tn.esprit.taktakandroid.models.login.LoginRequest
import tn.esprit.taktakandroid.models.login.LoginResponse
import tn.esprit.taktakandroid.models.sendOtp.SendOtpRequest
import tn.esprit.taktakandroid.models.sendOtp.SendOtpResponse

interface UserEndpoints {

    @POST("user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("user/sendOTP")
    suspend fun sendOtp(
        @Body request: SendOtpRequest
    ): Response<SendOtpResponse>
}