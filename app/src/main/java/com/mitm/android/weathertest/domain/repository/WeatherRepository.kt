package com.mitm.android.weathertest.domain.repository

 import com.google.android.gms.maps.model.LatLng
 import com.mitm.android.weathertest.common.Resource
 import com.mitm.android.weathertest.data.remote.DTO.today.WeatherTodayDTO
import com.mitm.android.weathertest.data.remote.DTO.week.WeatherWeekDTO

interface WeatherRepository {

    suspend fun getByLocationWeek(location: LatLng): WeatherWeekDTO

    suspend fun getByCityToday(city: String): WeatherTodayDTO
}