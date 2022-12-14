package com.example.ambuxproject.views.customer

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.ambuxproject.R
import com.example.ambuxproject.databinding.FragmentCustomerMapsBinding
import com.example.ambuxproject.others.TrackingUtility
import com.example.ambuxproject.viewmodel.AuthViewModel
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.errorprone.annotations.Var
import com.google.firebase.database.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import kotlin.collections.HashMap


class CustomerMapsFragment : Fragment() , EasyPermissions.PermissionCallbacks,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    com.google.android.gms.location.LocationListener, GeoQueryEventListener, ValueEventListener {

    private lateinit var binding : FragmentCustomerMapsBinding
    private lateinit var btnBookAmbulance : ExtendedFloatingActionButton
    private var radius = 1

    private lateinit var  lastLocation : Location
    private lateinit var map : GoogleMap
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var locationRequest:com.google.android.gms.location.LocationRequest

    private lateinit var authViewModel: AuthViewModel
    private lateinit var customerId : String

    private lateinit var customerDatabaseReference: DatabaseReference
    private lateinit var customerPickUpLocation : LatLng
    private lateinit var driverAvailableReference: DatabaseReference
    private lateinit var driverLocationReference: DatabaseReference

    private var driverReference : DatabaseReference? = null
    private var driverFound = false
    private var driverFoundId : String? = null
    private  var driverMarker : Marker? = null



    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        buildGoogleBuildApi()
        if(TrackingUtility.hasLocationPermissions(requireContext())){
            map.isMyLocationEnabled = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        driverAvailableReference = FirebaseDatabase.getInstance().reference.child("Driver Available")
        binding = FragmentCustomerMapsBinding.inflate(layoutInflater)
        btnBookAmbulance = binding.btnBookAmbulance
        authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        customerId = authViewModel.getCurrentUserId()!!
        customerDatabaseReference = FirebaseDatabase.getInstance().reference.child("Customer Request")
        driverLocationReference = FirebaseDatabase.getInstance().reference.child("Drivers Working")
        requestPermissions()

        btnBookAmbulance.setOnClickListener{
           val geoFire = GeoFire(customerDatabaseReference)  //  for Customer Location
            geoFire.setLocation(customerId, GeoLocation(lastLocation.latitude , lastLocation.longitude))
           customerPickUpLocation = LatLng(lastLocation.latitude,lastLocation.longitude)
            map.addMarker(MarkerOptions().position(customerPickUpLocation).title("Pick Up Customer here"))

            btnBookAmbulance.text = "Looking For Ambulance"
            getClosestAmbulance()
        }

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

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        TODO("Not yet implemented")
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            AppSettingsDialog.Builder(this).build().show()
        }else{
            requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
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
        TODO("Not yet implemented")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    override fun onLocationChanged(p0: Location?) {
        if (p0 != null) {
            lastLocation = p0
            var latLng = LatLng(p0.latitude,p0.longitude)
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            map.animateCamera(CameraUpdateFactory.zoomTo(16f))
        }

    }

    private fun buildGoogleBuildApi(){
        googleApiClient = GoogleApiClient.Builder(requireContext())
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        googleApiClient.connect()
    }

    override fun onStop() {

        super.onStop()
    }

    private fun getClosestAmbulance(){

        val geoFire = GeoFire(driverAvailableReference)   // for Available drivers Location
        val geoQuery = geoFire.queryAtLocation(GeoLocation(customerPickUpLocation.latitude,customerPickUpLocation.longitude),radius.toDouble())
         geoQuery.removeAllListeners()
        geoQuery.addGeoQueryEventListener(this)
        }

    override fun onKeyEntered(key: String?, location: GeoLocation?) {
        //for simplicity .... when geoQuery finds a query something this is executed
              if(!driverFound){
                  driverFound = true
                  driverFoundId = key   // id of the driver available

                  // for driver side
                  driverReference = FirebaseDatabase.getInstance().reference.child("Users").child("Drivers").child(driverFoundId!!)
                  val driverMap : HashMap<String,String> = HashMap<String,String>()
                  driverMap.put("CustomerRideId", customerId)
                  driverReference!!.updateChildren(driverMap as Map<String, Any>) // tc properly

                  gettingDriverLocation()  // to get the drivers location
                  btnBookAmbulance.text = "Looking For Driver"
              }
    }

    private fun gettingDriverLocation() {
           driverLocationReference.child("driverFoundId").child("l").addValueEventListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onDataChange(snapshot: DataSnapshot) {
        if(snapshot.exists()){
            val driverLocationMap = snapshot.value as List<Object>
            var locationLat: Double = 0.0
            var locationLong : Double = 0.0
            btnBookAmbulance.text = "Driver Found"

            locationLat = driverLocationMap[0].toString().toDouble()
            locationLong = driverLocationMap[1].toString().toDouble()

            val driverLatLong = LatLng(locationLat,locationLong)

            if (driverMarker != null){
                driverMarker?.remove()
            }

            driverMarker = map.addMarker(MarkerOptions().position(driverLatLong).title("Your driver is here"))

            val location1 : Location = Location("") // for customer
            location1.latitude = customerPickUpLocation.latitude
            location1.longitude = customerPickUpLocation.longitude

            val location2 : Location = Location("")
            location2.latitude = driverLatLong.latitude
            location2.longitude = driverLatLong.longitude

            val distance : Float = location1.distanceTo(location2);
            btnBookAmbulance.text = "Driver found - $distance"


        }


    }

    override fun onCancelled(error: DatabaseError) {
        TODO("Not yet implemented")
    }

    override fun onKeyExited(key: String?) {
        TODO("Not yet implemented")
    }

    override fun onKeyMoved(key: String?, location: GeoLocation?) {
        TODO("Not yet implemented")
    }

    override fun onGeoQueryReady() {
        if(!driverFound){
            radius += 1
            getClosestAmbulance()
        }
    }

    override fun onGeoQueryError(error: DatabaseError?) {
        TODO("Not yet implemented")
    }


    }



