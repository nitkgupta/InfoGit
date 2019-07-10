package com.nitkarsh.infogit.RestServices

import com.nitkarsh.infogit.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RestClient {

    companion object {

        private val retrofit by lazy {
            retrofit2.Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        private fun getOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(50, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val original = chain.request()
                    val request = original.newBuilder().build()
                    chain.proceed(request)
                }
                .build()
        }

        fun getClient(): Retrofit = retrofit
    }
}