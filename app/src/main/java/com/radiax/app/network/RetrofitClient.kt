package com.radiax.app.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit // ✅ Добавлен импорт TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8585/" // ✅ Правильный адрес для эмулятора

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // ⏳ Увеличиваем таймаут соединения
        .readTimeout(60, TimeUnit.SECONDS)    // ⏳ Увеличиваем таймаут чтения
        .writeTimeout(60, TimeUnit.SECONDS)   // ⏳ Увеличиваем таймаут записи
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val instance: RadiaXApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RadiaXApi::class.java)
    }
}