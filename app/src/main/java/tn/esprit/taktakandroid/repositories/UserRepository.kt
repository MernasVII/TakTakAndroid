package tn.esprit.taktakandroid.repositories

import tn.esprit.taktakandroid.api.RetrofitInstance
import tn.esprit.taktakandroid.models.login.LoginRequest
import tn.esprit.taktakandroid.models.ResetPwdRequest
import tn.esprit.taktakandroid.models.SendOtpRequest
import tn.esprit.taktakandroid.models.SignUpRequest
import tn.esprit.taktakandroid.models.updateprofile.UpdateProfileRequest
import tn.esprit.taktakandroid.models.updatepwd.UpdatePwdRequest
import java.util.*

class UserRepository {

    suspend fun login(request: LoginRequest) = RetrofitInstance.userApi.login(request)
    suspend fun sendOtp( sendOtpRequest: SendOtpRequest) =RetrofitInstance.userApi.sendOtp(sendOtpRequest)
    suspend fun resetPwd( resetPwdRequest: ResetPwdRequest) =RetrofitInstance.userApi.resetPwd(resetPwdRequest)
    suspend fun signUp( signUpRequest: SignUpRequest) =RetrofitInstance.userApi.signUp(signUpRequest)
    suspend fun loginWithGoogle(request: SignUpRequest) = RetrofitInstance.userApi.loginWithGoogle(request)

    fun generateOTP(): String {
        val random = Random()
        val otp = random.nextInt(10000)
        return String.format("%04d", otp)
    }

    suspend fun getSPsList(token:String)=
        RetrofitInstance.userApi.getSps(token)

    suspend fun getUserProfile(token:String)=
        RetrofitInstance.userApi.getUserProfile(token)

    suspend fun updateProfile(token:String, updateProfileRequest: UpdateProfileRequest)=
        RetrofitInstance.userApi.updateProfile(token,updateProfileRequest)

    suspend fun changepwd(token:String, updatePwdRequest: UpdatePwdRequest)=
        RetrofitInstance.userApi.changePwd(token,updatePwdRequest)
}