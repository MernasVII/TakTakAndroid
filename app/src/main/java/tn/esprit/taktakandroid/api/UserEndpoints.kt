package tn.esprit.taktakandroid.api

import retrofit2.Response
import retrofit2.http.*
import tn.esprit.taktakandroid.models.spslist.SPsResponse
import tn.esprit.taktakandroid.models.login.LoginRequest
import tn.esprit.taktakandroid.models.login.LoginResponse
import tn.esprit.taktakandroid.models.resetPwd.ResetPwdRequest
import tn.esprit.taktakandroid.models.resetPwd.ResetPwdResponse
import tn.esprit.taktakandroid.models.sendOtp.SendOtpRequest
import tn.esprit.taktakandroid.models.sendOtp.SendOtpResponse
import tn.esprit.taktakandroid.models.userprofile.UserProfileResponse

interface UserEndpoints {

    @POST("user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("user/sendOTP")
    suspend fun sendOtp(
        @Body request: SendOtpRequest
    ): Response<SendOtpResponse>

    @PUT("user/resetpwd")
    suspend fun resetPwd(
        @Body request: ResetPwdRequest
    ): Response<ResetPwdResponse>

    @GET("user/getAllSPs")
    suspend fun getSps(
        @Header("Authorization") token: String
    ): Response<SPsResponse>

    @GET("user/profile")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): Response<UserProfileResponse>

    @PUT("user/updateprofile")
    suspend fun updateProfile(
        @Header("Authorization") token: String
    ): Response<UserProfileResponse>
}