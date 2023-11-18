package com.example.transitapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.transitapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.transit.realtime.GtfsRealtime
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import java.net.URL


class TransitBusDataStream : Runnable {

    // Fetch GTFS data in a separate thread
    override fun run() {
        // URL for the Halifax GTFS Vehicle Position
        val url = URL("https://gtfs.halifax.ca/realtime/Vehicle/VehiclePositions.pb")
        val buses = mutableListOf<String>()
        val feed: GtfsRealtime.FeedMessage = GtfsRealtime.FeedMessage.parseFrom(url.openStream())

        // Iterate through vehicles in the feed
        for (entity in feed.entityList) {
            if (entity.hasVehicle()) {
                val tripUpdate = entity.vehicle.trip
                val position = entity.vehicle.position

                //Extract Route ID, Latitude, Longitude)
                val routeId = tripUpdate.routeId
                val latitude = position.latitude
                val longitude = position.longitude

                // Log the bus information
                Log.d("Bus Information","Route ID: $routeId, Latitude: $latitude, Longitude: $longitude")
            }
        }

        // Handle the fetched buses as needed
        Log.d("TransitBusDataStream", "Fetched buses: $buses")
    }
}
class MainActivity : AppCompatActivity() {

    var mapView: MapView? = null

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start fetching GTFS data on a new thread
        val dataStream = Thread(TransitBusDataStream())
        dataStream.start()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Initialize the Mapview
        mapView = findViewById(R.id.mapView)
        mapView?.getMapboxMap()?.loadStyleUri("mapbox://styles/mapbox/streets-v11")

        // Get location from Start Activity Intent
        val intent = intent
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        // Log the location in MainActivity
        Log.d("Location", "Latitude: $latitude, Longitude: $longitude")

        //Navigation set up - no need to make changes
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}