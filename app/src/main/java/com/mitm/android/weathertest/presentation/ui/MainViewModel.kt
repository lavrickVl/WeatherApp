package com.mitm.android.weathertest.presentation.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.mitm.android.weathertest.common.Constants
import com.mitm.android.weathertest.common.Logging
import com.mitm.android.weathertest.common.Resource
import com.mitm.android.weathertest.data.remote.DTO.today.WeatherTodayDTO
import com.mitm.android.weathertest.data.remote.DTO.week.WeatherWeekDTO
import com.mitm.android.weathertest.domain.usecases.GetWeatherTodayUseCase
import com.mitm.android.weathertest.domain.usecases.GetWeatherWeekUseCase
import dagger.hilt.android.internal.Contexts
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getWeatherTodayUseCase: GetWeatherTodayUseCase,
    private val getWeatherWeekUseCase: GetWeatherWeekUseCase
) : ViewModel() {
    var weatherToday: MutableLiveData<Resource<WeatherTodayDTO>> = MutableLiveData()

    private var _weatherWeek: MutableLiveData<Resource<WeatherWeekDTO>> = MutableLiveData()
    val weatherWeek: LiveData<Resource<WeatherWeekDTO>> = _weatherWeek


    private var _location: MutableLiveData<LatLng> = MutableLiveData(LatLng(0.0, 0.0))
    val location: LiveData<LatLng> = _location

    private var _city: MutableLiveData<String> = MutableLiveData()
    val city: LiveData<String> = _city


    private var _mainWeather: MutableLiveData<String> = MutableLiveData()
    val mainWeather: LiveData<String> = _mainWeather // for background


    init {
        _city.value = "Kiev"
        getWeatherToday()
    }


    fun saveCity(input_city: String) {
        _city.value = input_city
        getWeatherToday()
    }

    fun saveLocation(input_location: LatLng) {
        _location.value = input_location
    }

    fun getWeatherToday() = viewModelScope.launch {
        try {
            if (hasInternetConnection()) {
                if (city.value != null) {
                    weatherToday.postValue(Resource.Loading())
                    val response = getWeatherTodayUseCase.invoke(city.value!!)

                    _mainWeather.value =
                        weatherToday.value?.data?.weather?.first()?.description  // get description for background

                    weatherToday.postValue(Resource.Success(response))
                }
            } else {
                weatherToday.postValue(Resource.Error(Constants.ErrorMsg.ERROR_CONNECTION.msg))
            }


        } catch (ex: HttpException) {
            weatherToday.postValue(Resource.Error(ex.code().toString()))
            _city.value = ""
        } catch (ex: IOException) {
            weatherToday.postValue(Resource.Error(ex.message.toString()))
            _city.value = ""
        }

    }


    fun getWeatherWeek() = viewModelScope.launch {
        try {
            if (hasInternetConnection()) {
                if (location.value != null && city.value != null) {
                    val response = getWeatherWeekUseCase.invoke(location.value!!)
                    _weatherWeek.postValue(Resource.Success(response))
                }
            } else {
                weatherToday.postValue(Resource.Error(Constants.ErrorMsg.ERROR_CONNECTION.msg))
            }

        } catch (ex: HttpException) {
            weatherToday.postValue(Resource.Error(ex.code().toString()))
        } catch (ex: IOException) {
            weatherToday.postValue(Resource.Error(ex.message.toString()))
        }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = context?.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }

        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }

        return false
    }
}