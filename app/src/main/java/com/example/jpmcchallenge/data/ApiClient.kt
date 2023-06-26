package com.example.jpmcchallenge.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    val BASE_URL = "http://api.openweathermap.org"
    private var retrofit: Retrofit? = null

    fun getClient(): WeatherApi {
        if(retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(WeatherApi::class.java)
    }
}