package com.mitm.android.weathertest.domain.model

import com.mitm.android.weathertest.data.remote.DTO.today.*

data class TodayWeather(
    private val base: String,
    private val clouds: Clouds,
    private val cod: Int,
    private val coord: Coord,
    private val dt: Int,
    private val id: Int,
    private val main: Main,
    private val name: String,
    private val sys: Sys,
    private val timezone: Int,
    private val visibility: Int,
    private val weather: List<Weather>,
    private val wind: Wind
)
