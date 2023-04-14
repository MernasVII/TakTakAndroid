package tn.esprit.taktakandroid.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tn.esprit.taktakandroid.utils.Constants.BASE_URL

class RetrofitInstance {

    companion object {

        private val retrofit by lazy {

            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()) // used to determine how to interpret reponse and make it a kotlin object
                .client(client)
                .build()
        }
        val userApi: UserEndpoints by lazy {
            retrofit.create(UserEndpoints::class.java)
        }
        val notifApi: NotifEndpoints by lazy {
            retrofit.create(NotifEndpoints::class.java)
        }
        val aptApi: AptEndpoints by lazy {
            retrofit.create(AptEndpoints::class.java)
        }
        val requestApi: RequestEndpoints by lazy {
            retrofit.create(RequestEndpoints::class.java)
        }
    }
}