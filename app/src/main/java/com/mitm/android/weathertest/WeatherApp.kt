package com.mitm.android.weathertest

import android.app.Application
import android.util.Log
import com.mitm.android.weathertest.common.Constants.TAG
import com.mitm.android.weathertest.domain.repository.WeatherRepository
import dagger.hilt.EntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltAndroidApp
class WeatherApp: Application()