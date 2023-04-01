package com.bokoup.merchantapp.net

import com.bokoup.merchantapp.model.AppId
import com.bokoup.merchantapp.model.Constants
import com.bokoup.merchantapp.model.TxApiResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


class TransactionService(private val constants: Constants) {
    private val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.HEADERS
    }

    private val retrofit: Retrofit = Retrofit.Builder().baseUrl(this.constants.apiTx)
        .client(OkHttpClient.Builder().apply {addInterceptor(interceptor = interceptor)}.build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service: PromoService = retrofit.create(PromoService::class.java)
}

interface PromoService {
    @GET("promo/create")
    suspend fun getAppId(
    ): AppId

    @Multipart
    @Streaming
    @POST("promo/create/{payer}/{groupSeed}/{memo}")
    suspend fun createPromo(
        @Part metadata: MultipartBody.Part,
        @Part image: MultipartBody.Part,
        @Path("payer") payer: String,
        @Path("groupSeed") groupSeed: String,
        @Path("memo") memo: String?
    ): TxApiResponse

    @POST("promo/burn-delegated/{mint}/{tokenAccount}/{device}/{location}/{campaign}/{message}/{memo}")
    suspend fun burnDelegated(
        @Body accountData: AccountData,
        @Path("mint") mint: String,
        @Path("tokenAccount") tokenAccount: String,
        @Path("device") device: String,
        @Path("location") location: String,
        @Path("campaign") campaign: String,
        @Path("message") message: String,
        @Path("memo") memo: String?
    ): TxApiResponse
}
data class AccountData(val account: String)

