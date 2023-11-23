package com.example.transitapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mapboxMap: MapboxMap
    private lateinit var viewAnnotationManager: ViewAnnotationManager
    private fun addViewAnnotation(point: Point) {
        // Define the view annotation
        val viewAnnotation = viewAnnotationManager.addViewAnnotation(
            // Specify the layout resource id
            resId = R.layout.annotation,
            // Set any view annotation options
            options = viewAnnotationOptions {
                geometry(point)
            }
        )
        AnnotationBinding.bind(viewAnnotation)
    }

    private fun readGTFSData(){

        // Start fetching GTFS data on a new thread
        val dataStream = Thread(TransitBusDataStream())
        dataStream.start()


//        // Access the fetched data from TransitBusDataStream
//        val buses = (dataStream as TransitBusDataStream).buses
//
//        // Iterate through the data and add markers
//        for (bus in buses) {
//            addBusMarker(bus.routeId, bus.latitude, bus.longitude)
//        }

    }

    private fun addBusMarker(routeId: String, latitude: Double, longitude: Double) {
        val point = Point.fromLngLat(longitude, latitude)
        addViewAnnotation(point)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root



        // Initialize MapView and ViewAnnotationManager
        val mapView = root.findViewById<MapView>(R.id.mapView)
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
            mapboxMap = mapView.getMapboxMap()
            viewAnnotationManager = mapView.viewAnnotationManager

            // Get the center point of the map
            val center = mapboxMap.cameraState.center

            // Add the view annotation at the center point
            addViewAnnotation(center)

            // Read GTFS data and add bus markers
            readGTFSData()
        }

        //binding.textHome.text ="This is the home fragment"
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}