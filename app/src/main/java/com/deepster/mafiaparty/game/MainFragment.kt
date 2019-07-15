package com.deepster.mafiaparty.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.deepster.mafiaparty.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentMainBinding.inflate(inflater)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        viewModel.onUserLoggedOut.observe(viewLifecycleOwner, Observer { loggedOut ->
            if (loggedOut) {
                val action = MainFragmentDirections.actionMainFragmentToLoginFragment()
                binding.root.findNavController().navigate(action)
            }
        })

        viewModel.onNavigateToGame.observe(viewLifecycleOwner, Observer { joinMode ->
            if (null != joinMode) {
                val roomID = viewModel.roomID.value!!
                val action = when (joinMode) {
                    MainViewModel.JoinMode.GAME -> MainFragmentDirections.actionMainFragmentToGameFragment(roomID)
                    MainViewModel.JoinMode.LOBBY -> MainFragmentDirections.actionMainFragmentToLobbyFragment(roomID)
                }

                binding.root.findNavController().navigate(action)

            }
        })

        return binding.root
    }

}


