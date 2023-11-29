package com.example.transitapp.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.transitapp.R
import com.example.transitapp.databinding.FragmentDashboardBinding
import okio.IOException
import java.io.FileOutputStream

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var selectedRouteTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize AutoCompleteTextView
        val autoCompleteTextView = root.findViewById<AutoCompleteTextView>(R.id.busAutoCompleteTextView)
        val routeIds = resources.getStringArray(R.array.bus_route_ids)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, routeIds)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 1

        // Set a custom filter
        adapter.filter.filter(routeIds.toString())

        // Initialize search button
        val addButton = binding.buttonAdd
        addButton.setOnClickListener {

            // Get the selected route from AutoCompleteTextView
            val selectedRoute = autoCompleteTextView.text.toString()

            //val selectedRoute = autoCompleteTextView.text.toString()

            // Get the currently displayed routes from TextView
            val currentRoutes = selectedRouteTextView.text.toString()

            // Update the TextView with the selected route
            val updatedRoutes = if (currentRoutes.isNullOrEmpty()) selectedRoute else "$currentRoutes, $selectedRoute"
            selectedRouteTextView.text = updatedRoutes

            // Save the updated routes to internal storage
            saveRoutesToInternalStorage(updatedRoutes)


        }

        return root
    }

    private fun saveRoutesToInternalStorage(routes: String) {
        try {
            val fileName = "saved_routes.txt"
            val fileOutputStream: FileOutputStream = requireContext().openFileOutput(fileName, Context.MODE_PRIVATE)
            fileOutputStream.write(routes.toByteArray())
            fileOutputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}