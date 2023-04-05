package tn.esprit.taktakandroid.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import tn.esprit.taktakandroid.models.splist.SPsResponse
import tn.esprit.taktakandroid.models.login.LoginRequest
import tn.esprit.taktakandroid.models.login.LoginResponse
import tn.esprit.taktakandroid.models.userprofile.UserProfileResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import tn.esprit.taktakandroid.models.MessageResponse
import tn.esprit.taktakandroid.models.ResetPwdRequest
import tn.esprit.taktakandroid.models.SendOtpRequest
import tn.esprit.taktakandroid.models.SignUpRequest
import tn.esprit.taktakandroid.models.updateprofile.UpdateProfileRequest
import tn.esprit.taktakandroid.models.updatepwd.UpdatePwdRequest
import tn.esprit.taktakandroid.models.updateworkdesc.UpdateWorkDescRequest

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
}