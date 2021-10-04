package com.mitm.android.weathertest.data.remote.DTO.today

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)