package tn.esprit.taktakandroid.repositories

import tn.esprit.taktakandroid.api.RetrofitInstance
import tn.esprit.taktakandroid.models.Login.LoginRequest

class LoginRepository {

    suspend fun login(request: LoginRequest) = RetrofitInstance.userApi.login(request)

}