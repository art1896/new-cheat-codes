package com.cheatart.newcheatcodes.ui.popular

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import com.cheatart.newcheatcodes.R
import com.cheatart.newcheatcodes.adapter.GameAdapter
import com.cheatart.newcheatcodes.adapter.GameLoadStateAdapter
import com.cheatart.newcheatcodes.databinding.FragmentPopularGamesBinding
import com.cheatart.newcheatcodes.model.GameData
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PopularGamesFragment : Fragment(R.layout.fragment_popular_games),
    GameAdapter.OnItemClickListener {

    private var _binding: FragmentPopularGamesBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<PopularsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPopularGamesBinding.bind(view)
        viewModel.getPopularGames()
        val adapter = GameAdapter(this, requireContext())
        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.itemAnimator = DefaultItemAnimator()
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                header = GameLoadStateAdapter { adapter.retry() },
                footer = GameLoadStateAdapter { adapter.retry() }
            )
            buttonRetry.setOnClickListener { adapter.retry() }
        }
        viewModel.popularGamesResponse.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        adapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error

                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1
                ) {
                    recyclerView.isVisible = false
                }

            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(game: GameData) {
        val action =
            PopularGamesFragmentDirections.actionPopularGamesFragmentToPopularGameDetailsFragment(
                game.id!!,
                game.name!!
            )
        findNavController().navigate(action)

    }

}