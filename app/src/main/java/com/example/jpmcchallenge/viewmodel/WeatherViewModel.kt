package com.example.jpmcchallenge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.jpmcchallenge.data.ApiClient
import com.example.jpmcchallenge.data.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlin.math.roundToInt

class WeatherViewModel: ViewModel() {
    fun getWeather(city: String?) = liveData(Dispatchers.IO){
        emit(ApiResponse.loading(null))
        try {
            emit(ApiResponse.success(ApiClient().getClient().getForecast(city.toString())))
        } catch (e: Exception) {
            emit(ApiResponse.error(null,e.message ?: "Weather Api Error"))
        }
    }

    fun getCity(lat: String?, lon: String?) = liveData(Dispatchers.IO) {
        emit(ApiResponse.loading(null))
        try {
            emit(ApiResponse.success(ApiClient().getClient().getCity(lat,lon)))
        } catch (e: Exception) {
            emit(ApiResponse.error(null,e.message ?: "Geocoding Api Error"))
        }
    }

    fun inFahrenheit(kelvin: Double?): String {
        return if (kelvin != null) {
            ((kelvin - 273.15) * 9 / 5 + 32).roundToInt().toString()
        } else {
            "0"
        }
    }
}