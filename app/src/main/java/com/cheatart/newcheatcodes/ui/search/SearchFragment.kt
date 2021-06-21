package com.cheatart.newcheatcodes.ui.search

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.cheatart.newcheatcodes.R
import com.cheatart.newcheatcodes.adapter.GameAdapter
import com.cheatart.newcheatcodes.adapter.GameLoadStateAdapter
import com.cheatart.newcheatcodes.databinding.FragmentSearchBinding
import com.cheatart.newcheatcodes.model.GameData
import com.cheatart.newcheatcodes.utils.hideKeyboard
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search),
    GameAdapter.OnItemClickListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<SearchViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)

        viewModel.getPlatforms()
        viewModel.getGenres()

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

        val orderBy = resources.getStringArray(R.array.order_by)
        orderBy.forEach {
            addOrderByChipToGroup(it)
        }

        binding.searchButton.setOnClickListener {
            binding.recyclerView.scrollToPosition(0)
            hideKeyboard()
            val platformsId =
                if (binding.platformsChipGroup.checkedChipIds.isEmpty()) null else binding.platformsChipGroup.checkedChipIds.joinToString(
                    separator = ","
                )
            val ordering =
                (binding.orderByChipGroup.getChildAt(binding.orderByChipGroup.checkedChipId - 1)
                    ?.let {
                        (it as Chip).text.toString().lowercase()
                    })

            val genres =
                if (binding.genresChipGroup.checkedChipIds.isEmpty()) null else binding.genresChipGroup.checkedChipIds.joinToString(
                    separator = ","
                )
            val searchQuery = binding.searchQuery.text.toString()
            viewModel.getGames(
                platformsId = platformsId,
                ordering = ordering,
                genres = genres,
                searchQuery = searchQuery
            )


            if (binding.chipsLayout.visibility == View.VISIBLE) {
                binding.chipsLayout.visibility = View.GONE
                TransitionManager.beginDelayedTransition(binding.parentLayout, AutoTransition())
                binding.filterButton.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(),
                        R.anim.rotate_from_90
                    )
                )
            }
        }

        binding.filterButton.setOnClickListener {
            if (binding.chipsLayout.visibility == View.GONE) {
                TransitionManager.beginDelayedTransition(binding.parentLayout, AutoTransition())
                binding.chipsLayout.visibility = View.VISIBLE
                binding.filterButton.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(),
                        R.anim.rotate_to_90
                    )
                )

            } else {
                binding.chipsLayout.visibility = View.GONE
                TransitionManager.beginDelayedTransition(binding.parentLayout, AutoTransition())
                binding.filterButton.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireContext(),
                        R.anim.rotate_from_90
                    )
                )
            }
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

        viewModel.games.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        viewModel.genresResponse.observe(viewLifecycleOwner) {
            binding.genresChipGroup.removeAllViews()
            it.results.forEach { genre ->
                addGenreChipToGroup(genre)
            }
        }


        viewModel.platformsResponse.observe(viewLifecycleOwner) {
            binding.platformsChipGroup.removeAllViews()
            it.results.forEach { platform ->
                addPlatformChipToGroup(platform)
            }
        }

    }

    private fun addOrderByChipToGroup(name: String) {
        val chip = Chip(requireContext())
        val drawable =
            ChipDrawable.createFromAttributes(requireContext(), null, 0, R.style.CustomChipStyle)
        chip.setChipDrawable(drawable)
        chip.setTextAppearanceResource(R.style.ChipTextStyleAppearance)
        chip.text = name
        chip.isCheckable = true
        binding.orderByChipGroup.addView(chip as View)
    }

    private fun addPlatformChipToGroup(platform: GameData.Platforms) {
        val chip = Chip(requireContext())
        val drawable =
            ChipDrawable.createFromAttributes(requireContext(), null, 0, R.style.CustomChipStyle)
        chip.setChipDrawable(drawable)
        chip.setTextAppearanceResource(R.style.ChipTextStyleAppearance)
        chip.text = platform.name
        chip.id = platform.id!!
        chip.isCheckable = true
        binding.platformsChipGroup.addView(chip as View)
    }

    private fun addGenreChipToGroup(genre: GameData.Genre) {
        val chip = Chip(requireContext())
        val drawable =
            ChipDrawable.createFromAttributes(requireContext(), null, 0, R.style.CustomChipStyle)
        chip.setChipDrawable(drawable)
        chip.setTextAppearanceResource(R.style.ChipTextStyleAppearance)
        chip.text = genre.name
        chip.id = genre.id!!
        chip.isCheckable = true
        binding.genresChipGroup.addView(chip as View)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onItemClick(game: GameData) {
        val action = SearchFragmentDirections.actionSearchFragmentToPopularGameDetailsFragment(
            game,
            game.name!!
        )
        findNavController().navigate(action)

    }

}