package com.mitm.android.weathertest.common

import com.google.android.gms.maps.model.LatLng

object Constants {

    const val MAP_KEY = "MapViewBundleKey"

    const val TAG = "myLog"
    const val KELVIN = -273.15 // 0 C

    const val API_KEY = "5b87394ebb2699f0b7786b66885b8717"

    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    fun convertKelvinToCelsius(kelvin: Double): Int {
        return(kelvin + KELVIN).toInt()
    }


    enum class ErrorMsg(val msg: String) {
        ERROR_CONNECTION("Check your internet connection"),
        ERROR_INPUT("Incorrect input"),
        CHOOSE_CITY("Choose the city"),
    }
}