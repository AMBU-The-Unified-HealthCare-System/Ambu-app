package com.example.ambuxproject.views.customer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.ambuxproject.R
import com.example.ambuxproject.databinding.FragmentAppSettingsBinding
import com.example.ambuxproject.viewmodel.AuthViewModel


class AppSettingsFragment : Fragment() {

    private lateinit var binding : FragmentAppSettingsBinding
   private lateinit var authViewModel: AuthViewModel
   private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        binding = FragmentAppSettingsBinding.inflate(layoutInflater)

        binding.btnCustomerSignOut.setOnClickListener {
            authViewModel.signOut()
            navController.navigate(R.id.action_homeFragment_to_signUpFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }


}