package com.example.transitapp.ui.dashboard

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

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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

        // Set a custom filter that is case-insensitive and matches anywhere in the string
        adapter.filter.filter(routeIds.toString())

        // Initialize search button
        val searchButton = binding.buttonAdd
        searchButton.setOnClickListener {
            // Handle search button click
            val selectedRoute = autoCompleteTextView.text.toString()
            // Implement logic to handle the selected route (e.g., navigate to a new fragment)
            handleSelectedRoute(selectedRoute)
        }
//        binding.textDashboard.text="This is the dashboard fragment"
        return root
    }

    private fun handleSelectedRoute(routeId: String) {
        // Implement the logic to handle the selected route.
        // Assume to navigate to map fragment, displaying information
        // Log the selected route info.
        Log.d("SelectedRoute", "Route ID: $routeId")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}