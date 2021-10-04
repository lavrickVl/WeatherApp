package com.mitm.android.weathertest.data.repository

import com.google.android.gms.maps.model.LatLng
import com.mitm.android.weathertest.common.Resource
import com.mitm.android.weathertest.data.remote.DTO.today.WeatherTodayDTO
import com.mitm.android.weathertest.data.remote.DTO.week.WeatherWeekDTO
import com.mitm.android.weathertest.data.remote.WeatherApi
import com.mitm.android.weathertest.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
): WeatherRepository {


    override suspend fun getByLocationWeek(location: LatLng): WeatherWeekDTO {
        return api.getByLocationWeek(lat = location.latitude.toInt(), lon = location.longitude.toInt())
    }

    override suspend fun getByCityToday(city: String): WeatherTodayDTO {
        return api.getByCityToday(city)
    }


}