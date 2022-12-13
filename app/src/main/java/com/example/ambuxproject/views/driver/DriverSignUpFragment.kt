package com.example.ambuxproject.views.driver

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.ambuxproject.R
import com.example.ambuxproject.viewmodel.AuthViewModel


class DriverSignUpFragment : Fragment() {

    private lateinit var etUserEmail: EditText
    private lateinit var etUserPassword : EditText
    private lateinit var btnSignUp : Button
    private lateinit var tvSignInhere : TextView
    private lateinit var authViewModel : AuthViewModel
    private lateinit var navController: NavController


    override fun onCreateView (
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_driver_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etUserEmail = view.findViewById(R.id.et_user_mail)
        etUserPassword = view.findViewById(R.id.et_password)
        btnSignUp = view.findViewById(R.id.btn_register)
        tvSignInhere = view.findViewById(R.id.tv_login_state)
        navController = Navigation.findNavController(view)

        tvSignInhere.setOnClickListener {
            navController.navigate(R.id.action_driverSignUpFragment_to_driverSignInFragment)
        }

        btnSignUp.setOnClickListener {
            val email: String = etUserEmail.text.toString()
            val pass: String = etUserPassword.text.toString()

            if(email.isNotEmpty() && pass.isNotEmpty()){
                authViewModel.registerDriver(email,pass)
            }else{
                Toast.makeText(activity, "A", Toast.LENGTH_SHORT).show()
            }
        }


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        authViewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)

        authViewModel.getUserData().observe(viewLifecycleOwner, Observer{
                firebaseUser ->
            if(firebaseUser != null){
                navController.navigate(R.id.action_driverSignUpFragment_to_driverSignInFragment)
            }
        })
        super.onActivityCreated(savedInstanceState)
    }

}