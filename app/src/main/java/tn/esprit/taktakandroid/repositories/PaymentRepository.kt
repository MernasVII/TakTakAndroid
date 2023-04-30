package tn.esprit.taktakandroid.repositories

import tn.esprit.taktakandroid.api.RetrofitInstance
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.models.requests.InitPaymentRequest
import tn.esprit.taktakandroid.models.requests.SendLinkRequest

class PaymentRepository {
    //INIT PAYMENT
    suspend fun initPayment(token:String,initPaymentRequest: InitPaymentRequest)=
        RetrofitInstance.paymentApi.intiPayment(token,initPaymentRequest)

    //GET PAYMENT STATUS
    suspend fun paymentStatus(token:String,idBodyRequest: IdBodyRequest)=
        RetrofitInstance.paymentApi.paymentStatus(token,idBodyRequest)

    //SEND PAYMENT LINK
    suspend fun sendLink(token:String,sendLinkRequest: SendLinkRequest)=
        RetrofitInstance.paymentApi.sendLink(token,sendLinkRequest)
}