package com.example.ambuxproject.views


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.view.get
import com.example.ambuxproject.R
import com.example.ambuxproject.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

  private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding = FragmentHomeBinding.inflate(layoutInflater)


        // Todo implement the default selected item
        // for the first time when we will be opening the app we want to show the portion of ambulance/Map
        binding.bottomNavigationMenu.menu.findItem(R.id.fragment_person_details).setChecked(false)
        binding.bottomNavigationMenu. menu.findItem(R.id.fragmet_settings).setChecked(false)
        binding.bottomNavigationMenu.menu.findItem(R.id.fragment_map_main).setChecked(true)
        // Todo to be implemented
        replaceFragment(AppMainServicesHomeFragment())

        //trigger and navigate accordingly according to the item selected in bottom navigation menu
        binding.bottomNavigationMenu.setOnItemSelectedListener {
            when(it.itemId){
                R.id.fragmet_settings -> replaceFragment(AppSettingsFragment())
                R.id.fragment_map_main -> replaceFragment(AppMainServicesHomeFragment())
                R.id.fragment_person_details -> replaceFragment(UserDetailsFragment())
                else ->{

                }
            }
         true
        }


        return binding.root
    }



    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = childFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_container_of_home_fragment,fragment)
        fragmentTransaction.commit()
    }





}