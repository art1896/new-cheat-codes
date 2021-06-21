package com.cheatart.newcheatcodes.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.cheatart.newcheatcodes.R
import com.cheatart.newcheatcodes.adapter.CheatCodesAdapter
import com.cheatart.newcheatcodes.databinding.FragmentCheatDetailsBinding
import com.cheatart.newcheatcodes.model.Code
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.squareup.picasso.Picasso
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val PC = "PC"
private const val PS = "PS"
private const val XBOX = "Xbox"

class CheatDetailsFragment : Fragment(R.layout.fragment_cheat_details) {

    private var _binding: FragmentCheatDetailsBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<CheatDetailsFragmentArgs>()
    private lateinit var adapter: CheatCodesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCheatDetailsBinding.bind(view)
        setHasOptionsMenu(true)

        adapter = CheatCodesAdapter()
        MobileAds.initialize(requireContext()) {}
        val adRequest = AdRequest.Builder().build()
        binding.apply {
            cheatsRecyclerView.setHasFixedSize(true)
            cheatsRecyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            cheatsRecyclerView.adapter = adapter
            activityCheatsBanner.loadAd(adRequest)
            Picasso.get().load(args.cheat.poster).into(cheatPosterImage)
            cheatsCheatCount.text = args.cheat.codes.size.toString()
            cheatsGameName.text = args.cheat.name
            when (args.cheat.platform) {
                PC -> cheatsPlatformImage.setImageResource(R.drawable.ic_windows)
                PS -> cheatsPlatformImage.setImageResource(R.drawable.ic_playstation)
                XBOX -> cheatsPlatformImage.setImageResource(R.drawable.ic_xbox_icon)
            }

            lifecycleScope.launch {
                delay(800)
                adapter.setData(args.cheat.codes as MutableList<Code>)
                binding.shimmerViewContainer.stopShimmer()
                binding.shimmerViewContainer.isVisible = false
            }

            descriptionText.text = args.cheat.description

            infoButton.setOnClickListener {
                if (expandableView.visibility == View.GONE) {
                    TransitionManager.beginDelayedTransition(this.description, AutoTransition())
                    expandableView.visibility = View.VISIBLE
                    infoButton.setImageResource(R.drawable.info_sel)
                } else {
                    expandableView.visibility = View.GONE
                    TransitionManager.beginDelayedTransition(this.description, AutoTransition())
                    infoButton.setImageResource(R.drawable.ic_info)
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.search_menu, menu)
        val item = menu.findItem(R.id.action_search)
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
        val searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}