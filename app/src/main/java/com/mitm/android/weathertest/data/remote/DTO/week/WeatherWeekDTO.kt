package com.mitm.android.weathertest.data.remote.DTO.week

data class WeatherWeekDTO(
    val current: Current,
    val daily: List<Daily>,
    val lat: Int,
    val lon: Int,
    val timezone: String,
    val timezone_offset: Int
)