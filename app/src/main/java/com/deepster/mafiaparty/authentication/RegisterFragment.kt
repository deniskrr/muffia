package com.deepster.mafiaparty.authentication


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.deepster.mafiaparty.R
import com.deepster.mafiaparty.model.entities.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment() {
    companion object {
        val TAG = RegisterFragment::class.java.simpleName
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        if (auth.currentUser != null) { // User is logged in
            val action = RegisterFragmentDirections.actionRegisterFragmentToMainFragment()
            findNavController().navigate(action)
        }

        text_have_account.setOnClickListener { text ->
            val loginAction = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            text.findNavController().navigate(loginAction)
        }

        button_register.setOnClickListener { button ->
            button.isEnabled = false // Disable the register button while processing request

            // Get credentials
            val email = email_register.editText!!.text.toString()
            val username = username_register.editText!!.text.toString()
            if (username.trim().isBlank()) return@setOnClickListener
            // todo Additional checks

            val password = password_register.editText!!.text.toString()

            db.collection("users").whereEqualTo("username", username) // Check if the username exists in the db
                .get()
                .addOnSuccessListener { document ->
                    if (document.isEmpty) { // Username does not exist in the db
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                            if (task.isSuccessful) { // Successfully registered user
                                Log.d(TAG, "Register success")
                                // Add the user to the db
                                val uid = auth.currentUser!!.uid
                                val user = User(email, username, uid)
                                db.collection("users").document(uid).set(user)

                                // Sign in the user
                                val action = RegisterFragmentDirections.actionRegisterFragmentToMainFragment()
                                button.findNavController().navigate(action)
                            } else { // Failed to register user
                                Log.d(TAG, "Register failure")
                                button.isEnabled = true
                            }
                        }
                    } else { // Username exists in the db
                        // todo Show an error saying the username exists
                        Log.d(TAG, "Register failure")
                        button.isEnabled = true
                    }
                }
        }
    }


}
