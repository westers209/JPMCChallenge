package com.example.jpmcchallenge.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.jpmcchallenge.data.ApiResponse
import com.example.jpmcchallenge.data.ApiStatus
import com.example.jpmcchallenge.databinding.ActivityMainBinding
import com.example.jpmcchallenge.model.Forecast
import com.example.jpmcchallenge.viewmodel.WeatherViewModel
import com.squareup.picasso.Picasso
import java.text.DecimalFormat


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var lat: Double? = null
    private var lon: Double? = null
    private var cityName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        // Get city from shared preference
        val sp = getSharedPreferences("key", 0)
        val savedCity = sp.getString("cityName", "")

        // Load previously searched city. If the user has never searched then use their current location instead
        if(savedCity != ""){
            binding.etSearch.setText(savedCity)
            viewModel.getWeather(savedCity).observe(this) {
                onWeatherResult(it,viewModel)
            }
        } else {
            getLocationFromNetwork(viewModel)
        }

        // Saves searched city to shared preferences and fetches the city's weather data
        binding.btnSearch.setOnClickListener {
            // Save new city to shared preference
            val sedt = sp.edit()
            sedt.putString("cityName", binding.etSearch.text.toString())
            sedt.apply()
            viewModel.getWeather(binding.etSearch.text.toString()).observe(this) {
                onWeatherResult(it,viewModel)
            }
        }
    }

    // Prompts the user for location permission, then gets their location
    private fun getLocationFromNetwork(viewModel: WeatherViewModel) {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if(hasNetwork){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getLatLon(viewModel)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION) ,1) //request it
            }
        }
    }

    private fun getLatLon(viewModel: WeatherViewModel) {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val networkLocationListener = LocationListener{}
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, networkLocationListener)
            val lastKnownLocationByNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            lastKnownLocationByNetwork.let {
                val format = DecimalFormat("##.####")
                lat = it?.latitude
                lon = it?.longitude
                val roundedLat = format.format(lat)
                val roundedLon = format.format(lon)
                convertLatLon(roundedLat, roundedLon, viewModel)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        when (requestCode) {
            1 -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getLatLon(viewModel)
                } else {
                    Toast.makeText(this,"No Location Permission",Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
            }
        }
    }

    // Converts the location info into a city name, then populates the proper fields.
    private fun convertLatLon(lat: String?, lon: String?, viewModel: WeatherViewModel){
        viewModel.getCity(lat,lon).observe(this){ it ->
            it.let {apiResponse ->
                when(apiResponse.status){
                    ApiStatus.SUCCESS -> {
                        cityName = apiResponse.data?.get(0)?.name.toString()
                        binding.etSearch.setText(cityName)
                        viewModel.getWeather(cityName).observe(this){
                            onWeatherResult(it,viewModel)
                        }
                    }
                    ApiStatus.ERROR -> {
                        Toast.makeText(this,apiResponse.message,Toast.LENGTH_SHORT).show()
                    }
                    ApiStatus.LOADING -> {
                    }
                }
            }
        }
    }

    private fun onWeatherResult(apiResponse: ApiResponse<Forecast>, viewModel: WeatherViewModel) {
        when (apiResponse.status) {
            ApiStatus.SUCCESS -> {
                apiResponse.data.let {
                    Picasso.get()
                        .load("https://openweathermap.org/img/wn/${it?.weather?.get(0)?.icon}@2x.png")
                        .into(binding.ivIcon)
                    binding.tvMain.text = it?.weather?.get(0)?.main
                    binding.tvTemp.text = viewModel.inFahrenheit(it?.main?.temp)
                    binding.tvFeels.text = viewModel.inFahrenheit(it?.main?.feels_like)
                    binding.tvTempMax.text = viewModel.inFahrenheit(it?.main?.temp_max)
                    binding.tvTempMin.text = viewModel.inFahrenheit(it?.main?.temp_min)
                }
            }
            ApiStatus.ERROR -> {
            }
            ApiStatus.LOADING -> {
            }
        }
    }
}