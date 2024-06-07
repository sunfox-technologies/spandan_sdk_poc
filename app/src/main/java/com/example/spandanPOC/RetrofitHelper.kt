package com.example.spandanPOC

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST


class RetrofitHelper {

    fun getRetrofitInstance():TokenRefreshApi{
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.sunfox.in")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(TokenRefreshApi::class.java)
    }

    fun getRetrofitInstance1():EcgProcessor{
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.sunfox.in")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder()
                .setLenient()
                .create()))
            .build()
        return retrofit.create(EcgProcessor::class.java)
    }
}
class TokenRefreshResult(
    @SerializedName("message")
    var message: String,
    @SerializedName("token")
    var token: String,
)
interface TokenRefreshApi{
    @Headers("Authorization: 4u838u43u439u3","Used-Token: y7y6y5")
    @GET("/v2/spandan/token-refresh/")
    fun getToken():Call<TokenRefreshResult>
}

interface EcgProcessor{
    @Headers("Authorization:Bearer 4u838u43u439u3","Verifier-Token: TQOWT720uJ9koFYCbcY9WMNElIe/Gao8PWARzmi4NCP9x65dUbHW+NezsQ3Zo7LXtBjqeqaXmXFJr2H9opLxJg==","Content-Type: application/json")
    @POST("v2/spandan/ecg-processor")
    fun getReport(@Body ecgProcessorInput: EcgProcessorInput):Call<ReportResponse>
}