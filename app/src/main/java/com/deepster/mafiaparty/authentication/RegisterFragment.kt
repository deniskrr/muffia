package com.deepster.mafiaparty.authentication


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.deepster.mafiaparty.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private val viewModel: AuthViewModel by lazy {
        ViewModelProviders.of(this).get(AuthViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRegisterBinding.inflate(inflater)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        viewModel.onUserLoggedIn.observe(viewLifecycleOwner, Observer { loggedIn ->
            if (loggedIn) {
                val action = RegisterFragmentDirections.actionRegisterFragmentToMainFragment()
                binding.root.findNavController().navigate(action)
            }
        })

        binding.textHaveAccount.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToMainFragment()
            binding.root.findNavController().navigate(action)
        }
        return binding.root
    }


}
