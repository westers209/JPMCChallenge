package com.example.jpmcchallenge.model

// In a larger app I would separate out each model into different files. Here it's simple enough to just put all the models in one file
// Also I would assign @SerializedName for better readability.

data class Forecast(
    val weather: List<Weather>,
    val main: Temps
)

data class Weather(
    val main: String,
    val icon: String
)

data class Temps(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double
)

