package com.example.ambuxproject.views.driver

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.ambuxproject.R
import com.example.ambuxproject.databinding.FragmentDriverAppSettingsFragmentsBinding
import com.example.ambuxproject.viewmodel.AuthViewModel


class DriverAppSettingsFragments : Fragment() {

    private lateinit var binding : FragmentDriverAppSettingsFragmentsBinding
    private lateinit var navController: NavController
    private lateinit var authViewModel: AuthViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDriverAppSettingsFragmentsBinding.inflate(layoutInflater)
     authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)

        binding.btnSignOut.setOnClickListener {
            Log.d("Driverbhai/","Sign Out called")
            authViewModel.signOut()
            navController.navigate(R.id.action_homeFragmentDriver_to_driverSignUpFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

    }


}