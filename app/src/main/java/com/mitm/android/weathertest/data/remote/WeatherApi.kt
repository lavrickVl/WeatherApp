package com.mitm.android.weathertest.data.remote

import com.mitm.android.weathertest.common.Constants.API_KEY
import com.mitm.android.weathertest.data.remote.DTO.today.WeatherTodayDTO
import com.mitm.android.weathertest.data.remote.DTO.week.WeatherWeekDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {


    @GET("weather")
    suspend fun getByCityToday(@Query("q") city: String, @Query("appid") apiKey: String = API_KEY): WeatherTodayDTO

    @GET("onecall")
    suspend fun getByLocationWeek(@Query("exclude") exclude: String = "minutely,hourly,alerts",
                                  @Query("lat") lat: Int, @Query("lon") lon: Int,
                                  @Query("units") units: String = "metric",
                                  @Query("appid") apiKey: String = API_KEY): WeatherWeekDTO

}