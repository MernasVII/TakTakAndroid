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

interface WalletEndpoints {


    @GET("wallet/withdrawAmount")
    suspend fun withdrawAmount(
        @Header("Authorization") token: String
    ): Response<MessageResponse>

    @GET("wallet/getAmount")
    suspend fun getAmount(
        @Header("Authorization") token: String
    ): Response<WalletReqResp>

    @PUT("wallet/setAmount")
    suspend fun setAmount(
        @Header("Authorization") token: String,
        @Body request: WalletReqResp
    ): Response<MessageResponse>

}