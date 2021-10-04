package com.mitm.android.weathertest.domain.usecases

import com.mitm.android.weathertest.common.Resource
import com.mitm.android.weathertest.data.remote.DTO.today.WeatherTodayDTO
import com.mitm.android.weathertest.domain.repository.WeatherRepository
import javax.inject.Inject


class GetWeatherTodayUseCase @Inject constructor(private val repository: WeatherRepository) {

    suspend operator fun invoke(city: String): WeatherTodayDTO {
        return repository.getByCityToday(city)
    }

}