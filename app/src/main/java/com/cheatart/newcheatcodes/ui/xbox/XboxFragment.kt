package com.cheatart.newcheatcodes.ui.xbox

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.cheatart.newcheatcodes.R
import com.cheatart.newcheatcodes.adapter.CheatsAdapter
import com.cheatart.newcheatcodes.data.network.Resource
import com.cheatart.newcheatcodes.databinding.FragmentXboxBinding
import com.cheatart.newcheatcodes.model.CheatModel
import com.cheatart.newcheatcodes.ui.CheatViewModel
import com.cheatart.newcheatcodes.other.WrapContentLinearLayoutManager
import com.google.firebase.database.GenericTypeIndicator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class XboxFragment : Fragment(R.layout.fragment_xbox), CheatsAdapter.OnItemClickListener {

    private var _binding: FragmentXboxBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<CheatViewModel>()
    private lateinit var cheatsAdapter: CheatsAdapter
    private lateinit var cheatsList: MutableList<CheatModel>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentXboxBinding.bind(view)
        setHasOptionsMenu(true)

        cheatsAdapter = CheatsAdapter(this, requireContext())
        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = WrapContentLinearLayoutManager(requireContext())
            recyclerView.adapter = cheatsAdapter
        }
        viewModel.getXboxCheats()


        viewModel.xboxResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Success -> {
                    response.value.addOnSuccessListener { dataSnapshot ->
                        val t: GenericTypeIndicator<List<CheatModel>> =
                            object : GenericTypeIndicator<List<CheatModel>>() {}
                        cheatsList = dataSnapshot.getValue(t) as MutableList<CheatModel>
                        cheatsAdapter.setData(cheatsList)
                        binding?.shimmerViewContainer.stopShimmer()
                        binding?.shimmerViewContainer.isVisible = false

                    }
                }
                is Resource.Loading -> {
                }
                is Resource.Failure -> {
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu, menu)
        val item = menu.findItem(R.id.action_search)
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM)
        val searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                cheatsAdapter.filter.filter(newText)
                return false
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                cheatsList.sortBy { it.name }
                cheatsAdapter.notifyDataSetChanged()
                binding.recyclerView.smoothScrollToPosition(0)
                true
            }
            R.id.action_sort_by_date_created -> {
                cheatsList.sortByDescending { it.codes.size }
                cheatsAdapter.notifyDataSetChanged()
                binding.recyclerView.smoothScrollToPosition(0)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(cheat: CheatModel) {
        val action =
            XboxFragmentDirections.actionXboxFragmentToCheatDetailsFragment(cheat, cheat.name)
        findNavController().navigate(action)
    }
}