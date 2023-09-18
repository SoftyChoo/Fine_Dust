package com.example.fine_dust.retrofit

import com.example.fine_dust.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetWorkClient {

    private const val DUST_BASE_URL =
        "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/" //service URL

    //retrofit 그대로 가져다쓰기
    private fun createOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()

        if (BuildConfig.DEBUG) // 디버깅 용도
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        else
            interceptor.level = HttpLoggingInterceptor.Level.NONE

        return OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .addNetworkInterceptor(interceptor)
            .build()
    }

    private val dustRetrofit = Retrofit.Builder()
        .baseUrl(DUST_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()) // 컨버팅 : Json으로 쭉 들어온 데이터를 데이터 클래스로 바꿔줌
        .client(createOkHttpClient())
        .build()
    //retrofit


    val dustNetWork: NetWorkInterface = dustRetrofit.create(NetWorkInterface::class.java)

}