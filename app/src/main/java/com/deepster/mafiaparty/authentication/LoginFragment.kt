package com.deepster.mafiaparty.authentication


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.deepster.mafiaparty.databinding.FragmentLoginBinding


class LoginFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentLoginBinding.inflate(inflater)

        binding.lifecycleOwner = viewLifecycleOwner

        val viewModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)

        viewModel.onUserLoggedIn.observe(viewLifecycleOwner, Observer { loggedIn ->
            if (loggedIn) {
                val action = LoginFragmentDirections.actionLoginFragmentToMainFragment()
                binding.root.findNavController().navigate(action)
            }
        })

        binding.viewModel = viewModel

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


    }


}
