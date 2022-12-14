package com.example.ambuxproject.views.driver

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.provider.ContactsContract
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
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
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
    private var assignedCustomerRef: DatabaseReference? = null
    private lateinit var driverId : String
    private var customerId : String? = null
    private var assignedCustomerPickUpRef : DatabaseReference? = null


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

            // moving the camera to this particular location
            val latLng = LatLng(p0.latitude,p0.longitude)
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            map.animateCamera(CameraUpdateFactory.zoomTo(16f))

            val userId : String = authViewModel.getCurrentUserId()!! // get the  driver id
            val driverAvailabilityRef : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Driver Available") // Database Reference for the drivers who are available now
            val geoFireDriverAvailable= GeoFire(driverAvailabilityRef) // geofire for the driver available

            val driverWorkingRef = FirebaseDatabase.getInstance().reference.child("Drivers Working")  // Database Reference for the drivers who are working / booked drivers
            val geoFireDriversWorking  = GeoFire(driverWorkingRef)  // geofire for the working driver

            when (customerId) {
                "" -> {
                    geoFireDriversWorking.removeLocation(userId)
                    geoFireDriverAvailable.setLocation(
                        userId,
                        GeoLocation(p0.latitude, p0.longitude)
                    )
                }
                else -> {

                }

            }
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
        driverId = authViewModel.getCurrentUserId()!!

        getAssignedCustomerRequest()

        return binding.root
    }

    private fun getAssignedCustomerRequest() {
           assignedCustomerRef = FirebaseDatabase.getInstance().reference
               .child("Users").child("Drivers").child(driverId)
               .child("CustomerRideId")

            assignedCustomerRef!!.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        customerId = snapshot.value.toString()

                        getAssignedCustomerPickUpLocation()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }) // snapshot
    }

    private fun getAssignedCustomerPickUpLocation() {
        assignedCustomerPickUpRef = FirebaseDatabase.getInstance().reference
            .child("Customer Requests").child(customerId!!).child( "l")

        assignedCustomerPickUpRef!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){

                    val customerLocationMap : List<Object> = snapshot.getValue() as List<Object>
                    var locationLat: Double = 0.0
                    var locationLong : Double = 0.0

                    locationLat = customerLocationMap[0].toString().toDouble()
                    locationLong = customerLocationMap[1].toString().toDouble()

                    val driverLatLong = LatLng(locationLat,locationLong)
                    map.addMarker(MarkerOptions().position(driverLatLong).title("Pick Up Location"))

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


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