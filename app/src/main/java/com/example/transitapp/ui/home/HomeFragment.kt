package com.example.transitapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.transitapp.MainActivity
import com.example.transitapp.R
import com.example.transitapp.TransitBusDataStream
import com.example.transitapp.databinding.AnnotationBinding
import com.example.transitapp.databinding.FragmentHomeBinding
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.viewannotation.ViewAnnotationManager
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.example.transitapp.MainActivity.Bus
import com.google.transit.realtime.GtfsRealtime
import java.net.URL
import com.example.transitapp.StartActivity
import com.mapbox.maps.dsl.cameraOptions
import java.util.Timer
import java.util.TimerTask

var mapView: MapView? = null
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mapboxMap: MapboxMap
    private lateinit var viewAnnotationManager: ViewAnnotationManager

    private lateinit var timer: Timer
    private val refreshInterval: Long = 20000

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Allow network operations on the main thread
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // Initialize MapView and ViewAnnotationManager
        mapView = root.findViewById<MapView>(R.id.mapView)

        viewAnnotationManager = binding.mapView.viewAnnotationManager

        // Get location from MainActivity Intent
        val latitude = activity?.intent?.getDoubleExtra("latitude", 0.0)
        val longitude = activity?.intent?.getDoubleExtra("longitude", 0.0)

        mapboxMap = binding.mapView.getMapboxMap().apply {
            // Load a map style
            loadStyleUri(Style.MAPBOX_STREETS) {
            }
        }

        // Center the map on the received location
        mapboxMap.setCamera(
            cameraOptions {
                center(Point.fromLngLat(longitude ?: 0.0, latitude ?: 0.0))
                    .zoom(15.5)
                    .bearing(-17.6)
                    .build()
            })

        val url = URL("https://gtfs.halifax.ca/realtime/Vehicle/VehiclePositions.pb")
        val buses = mutableListOf<String>()
        val feed: GtfsRealtime.FeedMessage =
            GtfsRealtime.FeedMessage.parseFrom(url.openStream())

        // Iterate through vehicles in the feed
        for (entity in feed.entityList) {
            if (entity.hasVehicle()) {
                val tripUpdate = entity.vehicle.trip
                val position = entity.vehicle.position

                // Log the entire entity to understand its structure
                Log.d("GTFS", "Entity: $entity")
                // Extract Route ID, Latitude, Longitude)
                val routeId = tripUpdate.routeId
                val latitude = position.latitude
                val longitude = position.longitude

                // Create a Point for the current latitude and longitude
                val stop = Point.fromLngLat(longitude.toDouble(), latitude.toDouble())

                // Add view annotation for the stop
                addViewAnnotation(stop, routeId.toString())
            }
        }

        // Initialize the timer
        timer = Timer()

        // Schedule the timer task to refresh bus positions every 20 seconds
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                activity?.runOnUiThread {
                    refreshBusPositions()
                }
            }
        }, 0, refreshInterval)

        return root
    }

    private fun refreshBusPositions(){

    }
    private fun addViewAnnotation(point: Point, routeId: String) {

        // Define the view annotation
        val viewAnnotation = viewAnnotationManager.addViewAnnotation(
            // Specify the layout resource id
            resId = R.layout.annotation,
            // Set any view annotation options
            options = viewAnnotationOptions {
                geometry(point)
            }
        )

        //Set the routeId as the text of the textView in annotation layout
        viewAnnotation.findViewById<TextView>(R.id.annotation).text = routeId

        // Log to verify that the binding is happening
        Log.d("AddAnnotation", "Binding view annotation for Route ID: $routeId")
        AnnotationBinding.bind(viewAnnotation)

    }


    override fun onDestroyView() {
        super.onDestroyView()

        // Cancel the timer when the fragment is destroyed
        timer.cancel()

        _binding = null
    }
}