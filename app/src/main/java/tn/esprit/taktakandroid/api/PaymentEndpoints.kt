package tn.esprit.taktakandroid.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.models.requests.InitPaymentRequest
import tn.esprit.taktakandroid.models.requests.SendLinkRequest
import tn.esprit.taktakandroid.models.responses.InitPaymentResponse
import tn.esprit.taktakandroid.models.responses.MessageResponse
import tn.esprit.taktakandroid.models.responses.PaymentStatusResponse

interface PaymentEndpoints {
    //INIT PAYMENT
    @POST("payment/init")
    suspend fun intiPayment(
        @Header("Authorization") token: String,
        @Body request: InitPaymentRequest
    ): Response<InitPaymentResponse>

    //PAYMENT STATUS
    @POST("payment/status")
    suspend fun paymentStatus(
        @Header("Authorization") token: String,
        @Body request: IdBodyRequest
    ): Response<PaymentStatusResponse>

    @POST("payment/sendLink")
    suspend fun sendLink(
        @Header("Authorization") token: String,
        @Body request: SendLinkRequest
    ): Response<MessageResponse>
}