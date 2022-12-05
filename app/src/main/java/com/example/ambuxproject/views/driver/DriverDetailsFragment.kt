package com.example.ambuxproject.views.driver

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ambuxproject.R
import com.example.ambuxproject.databinding.FragmentDriverDetailsBinding


class DriverDetailsFragment : Fragment() {


    private lateinit var binding : FragmentDriverDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDriverDetailsBinding.inflate(layoutInflater)


        return binding.root
    }


}