package com.example.ambuxproject.views

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.ambuxproject.R

class HomeFragment : Fragment() {
   private var tvTest : TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tvTest = view.findViewById(R.id.tv_test)
        super.onViewCreated(view, savedInstanceState)
    }



}