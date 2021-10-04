package com.mitm.android.weathertest.presentation.ui.week

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.LatLng
import com.mitm.android.weathertest.R
import com.mitm.android.weathertest.common.Constants
import com.mitm.android.weathertest.common.Resource
import com.mitm.android.weathertest.data.remote.DTO.today.WeatherTodayDTO
import com.mitm.android.weathertest.data.remote.DTO.week.WeatherWeekDTO
import com.mitm.android.weathertest.databinding.FragmentWeekBinding
import com.mitm.android.weathertest.domain.usecases.GetWeatherWeekUseCase
import com.mitm.android.weathertest.presentation.ui.MainViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class WeekFragment : Fragment() {

    private var _binding: FragmentWeekBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by activityViewModels()


    private var toast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentWeekBinding.inflate(inflater, container, false)
        val root: View = binding.root



        mainViewModel.location.observe(viewLifecycleOwner, Observer { //need location for call
            mainViewModel.getWeatherWeek()
        })

        mainViewModel.weatherWeek.observe(viewLifecycleOwner, Observer { response ->
            //binding.textDashboard.text = "${it.lat.toString()}/${it.lon.toString()}"


            when (response) {
                is Resource.Success -> {
                    updateInfoWeekSuccess(response)
                }

                is Resource.Error -> {
                    showToast(response.message.toString())
                }

                is Resource.Loading -> showToast("Loading")

                else -> {

                }
            }
        })



        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun updateInfoWeekSuccess(response: Resource<WeatherWeekDTO>) {

        response.data?.let {
            binding.textDashboard.text = it.daily.first().temp.toString()
            binding.textView2.text = it.daily[1].temp.toString()
            binding.textView3.text = it.daily[2].temp.toString()
            binding.textView4.text = it.daily[3].temp.toString()
            binding.textView5.text = it.daily[4].temp.toString()
            binding.textView6.text = it.daily[5].temp.toString()
            binding.textView.text =  it.daily[6].temp.toString()


            val simpleDateFormat = SimpleDateFormat(
                "y, EEE, d MMMM, kk : mm",
                Locale.getDefault()
            ) //for formatting and parsing dates in a locale-sensitive manner


            val long = it.daily.first().dt.toLong() * 1000
            val date = Date()
            date.time = long

            val calendar = GregorianCalendar()
            calendar.timeInMillis = long


            showToast(simpleDateFormat.format(calendar.time))
        }

    }


    private fun showToast(msg: String) {
        if (toast != null) toast?.cancel()
        toast = Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT)
        toast?.show()
    }

}