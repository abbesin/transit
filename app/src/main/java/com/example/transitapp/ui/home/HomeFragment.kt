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

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var mapboxMap: MapboxMap
    private lateinit var viewAnnotationManager: ViewAnnotationManager

    private fun addViewAnnotation(point: Point, routeId: String) {
        // Log the function call
        Log.d("AddAnnotation", "Adding annotation for Route ID: $routeId")

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

    private fun readGTFSData() {
        Log.d("GTFS", "Start reading GTFS data...")
        // URL for the Halifax GTFS Vehicle Position
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

                // Log the bus information
                Log.d(
                    "Bus Info",
                    "Route ID: $routeId, Latitude: $latitude, Longitude: $longitude"
                )

                // Create a Point for the current latitude and longitude
                val stop = Point.fromLngLat(longitude.toDouble(), latitude.toDouble())

                // Add view annotation for the stop
                addViewAnnotation(stop, routeId.toString())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("HomeFragment", "Fragment created")
    }
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
        val mapView = root.findViewById<MapView>(R.id.mapView)
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
            mapboxMap = mapView.getMapboxMap()
            viewAnnotationManager = mapView.viewAnnotationManager

            // Get the center point of the map
            //val center = mapboxMap.cameraState.center

            // Get location from MainActivity Intent
            val latitude = activity?.intent?.getDoubleExtra("latitude", 0.0)
            val longitude = activity?.intent?.getDoubleExtra("longitude", 0.0)

            // Log the location in HomeFragment
            Log.d("HomeFragment", "Received Intent - Latitude: $latitude, Longitude: $longitude")

            // Log a message before reading GTFS data
            Log.d("GTFS", "Reading GTFS data...")

            // Read GTFS data and add bus markers
            readGTFSData()

            Log.d("GTFS", "After reading GTFS data...")

            // Center the map on the received location
            mapboxMap.setCamera(
                cameraOptions {
                    center(Point.fromLngLat(longitude ?: 0.0, latitude ?: 0.0))
                })
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}