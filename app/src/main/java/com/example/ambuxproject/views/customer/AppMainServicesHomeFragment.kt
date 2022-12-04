package com.example.ambuxproject.views.customer

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.ambuxproject.R
import com.example.ambuxproject.repository.Authentication


class AppMainServicesHomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//
//        Toast.makeText(activity,"${Authentication(application = Application()).getCurrentUserId()}",Toast.LENGTH_SHORT).show()
        return inflater.inflate(R.layout.fragment_app_main_services_home, container, false)
    }

}