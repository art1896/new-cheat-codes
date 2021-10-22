package com.cheatart.newcheatcodes.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cheatart.newcheatcodes.R
import com.cheatart.newcheatcodes.databinding.FragmentHomeBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        MobileAds.initialize(requireContext()) {}

        val adRequest = AdRequest.Builder().build()
        binding.apply {
            adView.loadAd(adRequest)
            pcCard.setOnClickListener { navigateToPc() }
            psCard.setOnClickListener { navigateToPs() }
            xboxCard.setOnClickListener { navigateToXbox() }
            searchCard.setOnClickListener { navigateToSearch() }
            popularCard.setOnClickListener { navigateToPopular() }
            favCard.setOnClickListener { navigateToFavorites() }
            exitCard.setOnClickListener {
                requireActivity().finish()
            }
        }
    }

    private fun navigateToFavorites() {
        val action = HomeFragmentDirections.actionHomeFragmentToFavoritesFragment()
        findNavController().navigate(action)
    }

    private fun navigateToSearch() {
        val action = HomeFragmentDirections.actionHomeFragmentToSearchFragment(0, null)
        findNavController().navigate(action)
    }

    private fun navigateToPopular() {
        val action = HomeFragmentDirections.actionHomeFragmentToPopularGamesFragment()
        findNavController().navigate(action)
    }

    private fun navigateToXbox() {
        val action = HomeFragmentDirections.actionHomeFragmentToXboxFragment()
        findNavController().navigate(action)
    }

    private fun navigateToPs() {
        val action = HomeFragmentDirections.actionHomeFragmentToPsFragment()
        findNavController().navigate(action)
    }

    private fun navigateToPc() {
        val action = HomeFragmentDirections.actionHomeFragmentToPcFragment()
        findNavController().navigate(action)
    }

}