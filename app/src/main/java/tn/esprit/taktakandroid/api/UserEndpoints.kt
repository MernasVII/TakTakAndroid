package tn.esprit.taktakandroid.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import tn.esprit.taktakandroid.models.responses.SPsResponse
import tn.esprit.taktakandroid.models.responses.LoginResponse
import tn.esprit.taktakandroid.models.responses.UserProfileResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import tn.esprit.taktakandroid.models.requests.*
import tn.esprit.taktakandroid.models.responses.MessageResponse

interface UserEndpoints {

    @POST("user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("user/sendOTP")
    suspend fun sendOtp(
        @Body request: SendOtpRequest
    ): Response<MessageResponse>

    @PUT("user/resetpwd")
    suspend fun resetPwd(
        @Body request: ResetPwdRequest
    ): Response<MessageResponse>

    @POST("user/signup")
    suspend fun signUp(
        @Body request: SignUpRequest
    ): Response<MessageResponse>
    @POST("user/loginWithGoogle")
    suspend fun loginWithGoogle(
        @Body request: SignUpRequest
    ): Response<LoginResponse>

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
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<MessageResponse>

    @Multipart
    @PUT("user/updateprofilepic")
    suspend fun updatePic(
        @Header("Authorization") token: String,
        @Part image:MultipartBody.Part
    ): Response<MessageResponse>

    @PUT("user/updateworkdesc")
    suspend fun updateWorkDesc(
        @Header("Authorization") token: String,
        @Body request: UpdateWorkDescRequest
    ): Response<MessageResponse>

    @PUT("user/changepwd")
    suspend fun changePwd(
        @Header("Authorization") token: String,
        @Body request: UpdatePwdRequest
    ): Response<MessageResponse>

    @DELETE("user/delete")
    suspend fun deleteUser(
        @Header("Authorization") token: String
    ): Response<MessageResponse>

    @POST("user/checkPwd")
    suspend fun checkPwd(
        @Header("Authorization") token: String,@Body request: CheckPwdRequest
    ): Response<MessageResponse>
}