package com.example.ambuxproject.views.customer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.ambuxproject.R
import com.example.ambuxproject.databinding.FragmentDriverOrCustomerBinding
import com.example.ambuxproject.databinding.FragmentHomeBinding
import com.example.ambuxproject.viewmodel.AuthViewModel


class DriverOrCustomerFragment : Fragment() {

    private lateinit var binding: FragmentDriverOrCustomerBinding

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDriverOrCustomerBinding.inflate(layoutInflater)


//        authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
      navController = Navigation.findNavController(view)
        super.onViewCreated(view, savedInstanceState)
        binding.btnCustomers.setOnClickListener {
            navController.navigate(R.id.action_driverOrCustomerFragment_to_signUpFragment)
        }

        binding.btnDrivers.setOnClickListener {
//            navController.navigate(R.id.action_driverOrCustomerFragment_to_driverSignInFragment)
            navController.navigate(R.id.action_driverOrCustomerFragment_to_driverSignUpFragment)
        }
    }



}