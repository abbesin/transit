package com.example.transitapp.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.transitapp.databinding.FragmentNotificationsBinding
import com.google.transit.realtime.GtfsRealtime
import java.net.URL


class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val linearLayout: LinearLayout = binding.linearLayout

        // Fetch alerts from the GTFS feed
        val url = URL("https://gtfs.halifax.ca/realtime/Alert/Alerts.pb")
        val feed: GtfsRealtime.FeedMessage = GtfsRealtime.FeedMessage.parseFrom(url.openStream())

        // Iterate through alerts in the feed and add them to the ScrollView
        for (entity in feed.entityList) {
            if (entity.hasAlert()) {
                val alert = entity.alert
                val alertDescription = alert.getDescriptionText()?.toString() ?: ""



                // Log the alert description
                Log.d("AlertDescription", "Alert Description: $alertDescription")

                // Create a TextView for the alert message
                val textView = TextView(requireContext())
                textView.text = alertDescription

                // Add the TextView to the ScrollView
                linearLayout.addView(textView)
            }
        }
                return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}