package tn.esprit.taktakandroid.repositories

import tn.esprit.taktakandroid.api.RetrofitInstance
import tn.esprit.taktakandroid.models.login.LoginRequest
import tn.esprit.taktakandroid.models.resetPwd.ResetPwdRequest
import tn.esprit.taktakandroid.models.sendOtp.SendOtpRequest
import tn.esprit.taktakandroid.models.signUp.SignUpRequest
import java.util.*

class UserRepository {

    suspend fun login(request: LoginRequest) = RetrofitInstance.userApi.login(request)
    suspend fun sendOtp( sendOtpRequest: SendOtpRequest) =RetrofitInstance.userApi.sendOtp(sendOtpRequest)
    suspend fun resetPwd( resetPwdRequest: ResetPwdRequest) =RetrofitInstance.userApi.resetPwd(resetPwdRequest)
    suspend fun signUp( signUpRequest: SignUpRequest) =RetrofitInstance.userApi.signUp(signUpRequest)




    fun generateOTP(): String {
        val random = Random()
        val otp = random.nextInt(10000)
        return String.format("%04d", otp)
    }
}