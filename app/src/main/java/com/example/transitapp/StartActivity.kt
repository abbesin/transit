package com.example.transitapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.OnSuccessListener
import com.google.transit.realtime.GtfsRealtime.FeedMessage
import java.net.URL


public class StartActivity : AppCompatActivity() {

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    companion object{private val REQUEST_CODE = 100}


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        Log.d("My App", "StartActivity created")
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // Verify the permissions granted
        if (requestCode == StartActivity.REQUEST_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation()
            }
        }
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission Granted - Get location from device
            Log.i("TESTING", "Permission granted!!")
            fusedLocationProviderClient?.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,null)
                ?.addOnSuccessListener(OnSuccessListener<Location> { location ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude

                        // Log the location
                        Log.i("TESTING", "Got the location!!")
                        Log.d("LocationStart", "Latitude: $latitude, Longitude: $longitude")

                        // Create an Intent to start the MainActivity
                        val intent = Intent(this@StartActivity, MainActivity::class.java)

                        // Include location data in the Intent
                        intent.putExtra("latitude", latitude)
                        intent.putExtra("longitude", longitude)

                        // Start the MainActivity with the Intent
                        startActivity(intent)

                    } else {
                        // Handle the case where location is null
                        Log.e("TESTING", "Last known location is null")
                    }
                })
        } else {
            // Permission Denied - Ask the user for permission
            askPermission()
        }
    }

    private fun askPermission() {

        // Display screen to request permission
        ActivityCompat.requestPermissions(
            this, arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
            StartActivity.REQUEST_CODE
        )
    }

    object GtfsRealtimeExample {
        @Throws(Exception::class)
        @JvmStatic
        fun main() {
            val url = URL("https://gtfs.halifax.ca/realtime/Vehicle/VehiclePositions.pb")
            val feed = FeedMessage.parseFrom(url.openStream())
            for (entity in feed.entityList) {
                if (entity.hasTripUpdate()) {
                    println(entity.tripUpdate)
                }
            }
        }
    }
}