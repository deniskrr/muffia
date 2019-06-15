package com.deepster.mafiaparty.login


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController

import com.deepster.mafiaparty.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment : androidx.fragment.app.Fragment() {

    companion object {
        val TAG = LoginFragment::class.java.simpleName
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) { // User is logged in
            val action = LoginFragmentDirections.actionLoginFragmentToMainFragment()
            findNavController().navigate(action)
        }

        button_login.setOnClickListener { button ->
            button.isEnabled = false
            val email = email_login.editText!!.text.toString()
            val password = password_login.editText!!.text.toString()

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Sign in success")

                    // Sign in the user
                    val action = LoginFragmentDirections.actionLoginFragmentToMainFragment()
                    button.findNavController().navigate(action)

                } else {
                    Log.d(TAG, "Sign in failure")
                    button.isEnabled = true
                }
            }
        }

    }


}
