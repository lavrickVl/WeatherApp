package com.mitm.android.weathertest.presentation.ui.today

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import com.mitm.android.weathertest.R
import com.mitm.android.weathertest.common.Constants
import com.mitm.android.weathertest.common.Logging
import com.mitm.android.weathertest.common.Resource
import com.mitm.android.weathertest.data.remote.DTO.today.WeatherTodayDTO
import com.mitm.android.weathertest.databinding.FragmentTodayBinding
import com.mitm.android.weathertest.presentation.ui.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.*

class TodayFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentTodayBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()

    private var toast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTodayBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setHasOptionsMenu(true)

        mainViewModel.weatherToday.observe(viewLifecycleOwner, Observer { response ->

            when (response) {
                is Resource.Success -> {
                    updateInfoSuccess(response)
                }

                is Resource.Error -> {
                    showToast(response.message.toString())
                }

                is Resource.Loading -> showToast("Loading")
            }
        })


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)

        val search = menu.findItem(R.id.search_menu_today)
        val searchView = search?.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query?.length!! > 3) {
            getLocationByCity(query)
        }

        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        return true
    }


    fun getLocationByCity(city: String) {
        try {
            val geocoder = Geocoder(requireActivity())
            val responseLocation = geocoder.getFromLocationName(city, 1)

            if (responseLocation.isNotEmpty()) {
                val location =
                    LatLng(responseLocation.first().latitude, responseLocation.first().longitude)

                mainViewModel.saveLocation(location)
                mainViewModel.saveCity(city)
            } else {
                mainViewModel.weatherToday.postValue(Resource.Error(Constants.ErrorMsg.ERROR_INPUT.msg))
            }

        } catch (ex: IOException) {
            mainViewModel.weatherToday.postValue(Resource.Error(Constants.ErrorMsg.ERROR_CONNECTION.msg))
        } catch (ex: IllegalArgumentException) {
            mainViewModel.weatherToday.postValue(Resource.Error(Constants.ErrorMsg.ERROR_INPUT.msg))
        }

    }


    private fun showToast(msg: String) {
        if (toast != null) toast?.cancel()
        toast = Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT)
        toast?.show()
    }


    private fun updateInfoSuccess(response: Resource<WeatherTodayDTO>) {

        val celsius = Constants.convertKelvinToCelsius(response.data?.main?.temp ?: 0.0)
        binding.tvTemp.text = getString(R.string.celsius, celsius)

        response.data?.let {
            binding.tvCity.text = it.name.apply { first().uppercase() }
            binding.tvMain.text = it.weather.first().main
            binding.tvHumidity.text = getString(R.string.humidity, it.main.humidity)
            binding.tvWind.text = getString(R.string.wind, it.wind.speed)
            binding.tvPressuare.text = getString(R.string.pressure, it.main.pressure)
        }

    }


    private fun updateBackground(weather: String) {
        when (weather) {
            "Clouds" -> binding.imageView.setImageResource(R.drawable.cloudy)
            "Rain" -> binding.imageView.setImageResource(R.drawable.rainy)
            else -> binding.imageView.setImageResource(R.drawable.good)
        }
    }
}