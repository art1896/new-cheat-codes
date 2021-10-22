package com.cheatart.newcheatcodes.ui.favorites

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import com.cheatart.newcheatcodes.R
import com.cheatart.newcheatcodes.adapter.FavoritesAdapter
import com.cheatart.newcheatcodes.adapter.GameAdapter
import com.cheatart.newcheatcodes.adapter.GameLoadStateAdapter
import com.cheatart.newcheatcodes.databinding.FragmentFavoritesBinding
import com.cheatart.newcheatcodes.db.Game
import com.cheatart.newcheatcodes.ui.popular.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.fragment_favorites), FavoritesAdapter.OnItemClickListener {

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<DetailsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoritesBinding.bind(view)

        val adapter = FavoritesAdapter(this)
        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.itemAnimator = DefaultItemAnimator()
            recyclerView.adapter = adapter
        }
        viewModel.getAllGames().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(game: Game) {
        val action = FavoritesFragmentDirections.actionFavoritesFragmentToPopularGameDetailsFragment(game.id!!, game.gameName!!)
        findNavController().navigate(action)
    }
}