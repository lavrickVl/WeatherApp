package com.mitm.android.weathertest.di

import com.mitm.android.weathertest.common.Constants.BASE_URL
import com.mitm.android.weathertest.data.remote.WeatherApi
import com.mitm.android.weathertest.data.repository.WeatherRepositoryImpl
import com.mitm.android.weathertest.domain.repository.WeatherRepository
import com.mitm.android.weathertest.domain.usecases.GetWeatherTodayUseCase
import com.mitm.android.weathertest.domain.usecases.GetWeatherWeekUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providerApi(): WeatherApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    @Provides
    @Singleton
    fun providerRepository(api: WeatherApi): WeatherRepository {
        return WeatherRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideGetWeatherTodayUseCase(repository: WeatherRepository): GetWeatherTodayUseCase {
        return GetWeatherTodayUseCase(repository)
    }


}