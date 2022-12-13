package com.example.ambuxproject.views.driver

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.example.ambuxproject.R
import com.example.ambuxproject.databinding.FragmentHomeDriverBinding
import com.example.ambuxproject.viewmodel.AuthViewModel


class HomeFragmentDriver : Fragment() {

    private lateinit var binding: FragmentHomeDriverBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeDriverBinding.inflate(layoutInflater)


        // Todo implement the default selected item
        // for the first time when we will be opening the app we want to show the portion of ambulance/Map
        binding.bottomNavigationMenu.menu.findItem(R.id.fragment_person_details).setChecked(false)
        binding.bottomNavigationMenu. menu.findItem(R.id.fragmet_settings).setChecked(false)
        binding.bottomNavigationMenu.menu.findItem(R.id.fragment_map_main).setChecked(true)
        // Todo to be implemented
        replaceFragment(DriverMapsFragment())

        //trigger and navigate accordingly according to the item selected in bottom navigation menu
        binding.bottomNavigationMenu.setOnItemSelectedListener {
            when(it.itemId){
                R.id.fragmet_settings -> replaceFragment(DriverAppSettingsFragments())
                R.id.fragment_map_main -> replaceFragment(DriverMapsFragment())
                R.id.fragment_person_details -> replaceFragment(DriverDetailsFragment())
                else ->{

                }
            }
            true
        }
         authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        return binding.root
    }



    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = childFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_container_of_home_fragment,fragment)
        fragmentTransaction.commit()
    }

    override fun onStop() {

        super.onStop()
    }
}