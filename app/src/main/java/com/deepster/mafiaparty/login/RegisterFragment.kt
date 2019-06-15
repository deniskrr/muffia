package com.deepster.mafiaparty.login


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.deepster.mafiaparty.R
import com.deepster.mafiaparty.model.User
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
            //TODO Move to main fragment
        }

        button_register.setOnClickListener { button ->
            button.isEnabled = false // Disable the register button while processing request

            //Get credentials
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
                                val user = User(email, username, auth.currentUser!!.uid)
                                db.collection("users").document(username).set(user)
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
