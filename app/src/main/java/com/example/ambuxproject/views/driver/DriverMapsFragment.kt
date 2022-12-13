package com.example.ambuxproject.views.driver

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.ambuxproject.R
import com.example.ambuxproject.databinding.FragmentDriverMapsBinding
import com.example.ambuxproject.others.TrackingUtility
import com.example.ambuxproject.viewmodel.AuthViewModel
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.currentCoroutineContext
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions


class DriverMapsFragment : Fragment() ,EasyPermissions.PermissionCallbacks,
GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private lateinit var binding  : FragmentDriverMapsBinding
    private lateinit var  lastLocation : Location
    private lateinit var map : GoogleMap
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var locationRequest:com.google.android.gms.location.LocationRequest
    private lateinit var authViewModel : AuthViewModel




    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback {
            googleMap ->
        map = googleMap
        buildGoogleBuildApi()
        if(TrackingUtility.hasLocationPermissions(requireContext())){
            map.isMyLocationEnabled = true
        }
    }

    @SuppressLint("MissingPermission")
    override fun onConnected(p0: Bundle?) {

        locationRequest = com.google.android.gms.location.LocationRequest()
        locationRequest.apply {
            interval = 1000
            fastestInterval = 1000
            priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        if(!TrackingUtility.hasLocationPermissions(requireContext())){
            return
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this)

    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onLocationChanged(p0: Location?) {
        if (p0 != null) {
            lastLocation = p0
            var latLng = LatLng(p0.latitude,p0.longitude)
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            map.animateCamera(CameraUpdateFactory.zoomTo(16f))

            val userId : String = authViewModel.getCurrentUserId()!!
            val driverAvailabilityRef : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Driver Available")
            val geoFire : GeoFire = GeoFire(driverAvailabilityRef)

            geoFire.setLocation(userId, GeoLocation(p0.latitude,p0.longitude))
        }
    }

    private fun buildGoogleBuildApi(){
        googleApiClient = GoogleApiClient.Builder(requireContext())
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        googleApiClient.connect()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View? {
        requestPermissions()
        binding = FragmentDriverMapsBinding.inflate(layoutInflater)
        authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        mapFragment?.getMapAsync(callback)
    }

        private fun requestPermissions(){
        if(TrackingUtility.hasLocationPermissions(requireContext())){
            return
        }
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
         EasyPermissions.requestPermissions(
             this,
             "You need to accept the permissions to use this app",
             0,
             Manifest.permission.ACCESS_COARSE_LOCATION,
             Manifest.permission.ACCESS_FINE_LOCATION
         ) } else{
                EasyPermissions.requestPermissions(
                    this,
                    "You need to accept the permissions to use this app",
                    0,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }else{
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        TODO("Not yet implemented")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }

    override fun onStop() {
        super.onStop()
        Log.d("Driverbhai/","On Stop Invoked")
        disconnectDriver()
    }

    private fun disconnectDriver(){
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
          val userId : String = authViewModel.getCurrentUserId()!!
         val driverAvailabilityRef : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Driver Available")
         val geoFire : GeoFire = GeoFire(driverAvailabilityRef)
        Log.d("Driverbhai/", userId)
        geoFire.removeLocation(userId)
        Log.d("Driverbhai/","Disconnect Driver called")
      }

    }