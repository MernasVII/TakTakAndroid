package tn.esprit.taktakandroid.repositories

import tn.esprit.taktakandroid.api.RetrofitInstance
import tn.esprit.taktakandroid.models.requests.IdBodyRequest
import tn.esprit.taktakandroid.models.requests.MakeBidRequest
import tn.esprit.taktakandroid.models.requests.WalletReqResp

class WalletRepository {
    suspend fun getBalance(token:String)=
        RetrofitInstance.walletApi.getAmount(token)


    suspend fun withdrawMoney(token:String)=
        RetrofitInstance.walletApi.withdrawAmount(token)

    suspend fun addAmountToWallet(token:String,walletReqResp: WalletReqResp)=
        RetrofitInstance.walletApi.setAmount(token, walletReqResp)
}