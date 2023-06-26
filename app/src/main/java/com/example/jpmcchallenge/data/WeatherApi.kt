package com.example.jpmcchallenge.data

import com.example.jpmcchallenge.model.City
import com.example.jpmcchallenge.model.Forecast
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherApi {
    @GET("/data/2.5/weather")
    suspend fun getForecast(
        @Query("q", encoded = false) city: String,
        @Query("appid") apiKey: String = "df4a56e34fd1d4d7c0f16cfe2d11bec1"
    )
    : Forecast

    @GET("/geo/1.0/reverse")
    suspend fun getCity(
        @Query("lat") lat: String?,
        @Query("lon") lon: String?,
        @Query("appid") apiKey: String = "df4a56e34fd1d4d7c0f16cfe2d11bec1"
    ) : List<City>
}
