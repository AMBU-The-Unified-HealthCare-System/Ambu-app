package com.example.ambuxproject.views.driver

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ambuxproject.R
import com.example.ambuxproject.databinding.FragmentDriverAppSettingsFragmentsBinding


class DriverAppSettingsFragments : Fragment() {

    private lateinit var binding : FragmentDriverAppSettingsFragmentsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDriverAppSettingsFragmentsBinding.inflate(layoutInflater)

        return inflater.inflate(R.layout.fragment_driver_app_settings_fragments, container, false)
    }


}