package com.mitm.android.weathertest.presentation.ui.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.mitm.android.weathertest.common.Logging
import com.mitm.android.weathertest.databinding.FragmentMapBinding

import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.*
import java.lang.Exception
import java.util.*
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.material.snackbar.Snackbar
import com.mitm.android.weathertest.R
import com.mitm.android.weathertest.common.Constants
import com.mitm.android.weathertest.presentation.MainActivity
import com.mitm.android.weathertest.presentation.ui.MainViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.lang.IllegalArgumentException


class MapFragment : Fragment(), SearchView.OnQueryTextListener {

    private val mainViewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    //maps items
    private var map: GoogleMap? = null
    private lateinit var mapView: MapView

    //permission checker
    private var locationPermissionGranted = false
    private var gpsStatus = false


    private var toast: Toast? = null
    private var city = ""
    private var location = LatLng(0.0, 0.0)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setHasOptionsMenu(true)

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)


        mainViewModel.city.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            getLocationByCity(it)
        })

        mapView.getMapAsync(OnMapReadyCallback {
            this.map = it

            getCity(location) // if city set earlier, show it

            it.setOnMapClickListener {
                getCity(it)
            }

            setUpMap()
        })

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        val search = menu.findItem(R.id.search_menu)
        val searchView = search?.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mapView
    }


    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }




    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                //lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
            return
        }

        showLocationButton()
        map?.isMyLocationEnabled = true
    }


    fun showLocationButton() {
        val locationButton =
            (mapView.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(
                Integer.parseInt("2")
            )
        val rlp = locationButton.layoutParams as (RelativeLayout.LayoutParams)
        // position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
        rlp.setMargins(0, 0, 30, 30);

        locationButton.setOnClickListener {
            Logging(msg = "LocButton")
        }
    }


    fun geoCoder(_location: LatLng): Boolean {
        try {
            val geocoder = Geocoder(requireActivity(), Locale.getDefault())
            val addresses: List<Address> =
                geocoder.getFromLocation(_location.latitude, _location.longitude, 1)
            return if (addresses[0].getLocality() != null) {
                mainViewModel.saveLocation(location) // save location for the week
                city = addresses[0].getLocality()
                true
            } else false
        } catch (ex: Exception) {

            return false
        } catch (ex: HttpException) {

            return false
        }
    }


    fun getCity(_location: LatLng) {
        if (geoCoder(_location)) {
            showCity(_location)
            addMarker(_location)
        } else {
            showToast(Constants.ErrorMsg.CHOOSE_CITY.msg)
        }
    }

    private fun showCity(location: LatLng) {
        val cameraPosition = CameraPosition.Builder()
            .target(LatLng(location.latitude, location.longitude))
            .zoom(8f)
            .build()
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
        map?.animateCamera(cameraUpdate)
    }

    fun addMarker(location: LatLng) {
        map?.clear()
        map?.addMarker(
            MarkerOptions()
                .position(location)
                .title("$city")
                .snippet("$location")
        )

        map?.setOnMarkerClickListener {
            it.showInfoWindow()
            showSnackBar(city)
            true
        }

        mainViewModel.saveCity(city)
        showSnackBar(city)
    }


    fun showSnackBar(city: String) {
        val mySnackbar = Snackbar.make(requireView(), city, Snackbar.LENGTH_LONG)

        mySnackbar.setAction("Next", View.OnClickListener {
            val toast = Toast.makeText(requireActivity(), "Next clicked!", Toast.LENGTH_LONG)
            toast.show()

            goToTodayFgmnt()
        })


        mySnackbar.show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        try {
            if (query?.length!! > 3) {
                lifecycleScope.launch { getLocationByCity(query) }
            }
        } catch (ex: IOException) {
            showToast(ex.message.toString())  // todo incorrect msg
        }

        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        return true
    }


    fun goToTodayFgmnt() {
        findNavController().navigate(R.id.action_mapFragment_to_todayFragment)
    }


    fun getLocationByCity(city: String) {
        try {
            val geocoder = Geocoder(requireActivity())
            val responseLocation = geocoder.getFromLocationName(city, 5)

            if (responseLocation.isNotEmpty()) {
                Log.d(TAG, "onQueryTextSubmit: $responseLocation")
                location =
                    LatLng(responseLocation.first().latitude, responseLocation.first().longitude)
                mainViewModel.saveLocation(location)
            } else showToast(Constants.ErrorMsg.ERROR_INPUT.msg)


        } catch (ex: IOException) {
            Logging("getLocationByCity IO err: ${ex.message}")
        } catch (ex: IllegalArgumentException) {
            Logging("getLocationByCity IllArg err: ${ex.message}")
        }

    }


    private fun showToast(msg: String) {
        if (toast != null) toast?.cancel()
        toast = Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT)
        toast?.show()
    }


    companion object {
        private val TAG = "myLogMap"
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }
}