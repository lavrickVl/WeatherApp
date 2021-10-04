package com.mitm.android.weathertest.domain.usecases

import com.google.android.gms.maps.model.LatLng
import com.mitm.android.weathertest.common.Resource
import com.mitm.android.weathertest.data.remote.DTO.today.WeatherTodayDTO
import com.mitm.android.weathertest.data.remote.DTO.week.WeatherWeekDTO
import com.mitm.android.weathertest.domain.repository.WeatherRepository
import javax.inject.Inject


class GetWeatherWeekUseCase @Inject constructor(private val repository: WeatherRepository) {

    suspend operator fun invoke(location: LatLng): WeatherWeekDTO {
        return repository.getByLocationWeek(location)
    }

}