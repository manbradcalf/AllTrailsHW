package com.benmedcalf.alltrailshomework.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.benmedcalf.alltrailshomework.R
import com.benmedcalf.alltrailshomework.databinding.FragmentItemListBinding
import com.benmedcalf.alltrailshomework.model.Restaurant
import com.benmedcalf.alltrailshomework.viewmodel.BaseViewModel
import com.benmedcalf.alltrailshomework.viewmodel.ListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListResultsFragment : Fragment(R.layout.fragment_item_list) {
    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!
    private val listViewModel: ListViewModel by viewModels()
    private var searchResults = arrayListOf<Restaurant>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemListBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        binding.loadingIndicatorList.visibility = View.VISIBLE
        val navController = findNavController()
        val listAdapter = RestaurantRecyclerViewAdapter(searchResults, navController)
        listAdapter.updateFavoriteStatus = listViewModel.onFavoriteClick

        with(binding.list) {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }

        binding.goToMapButton.setOnClickListener {
            navController.popBackStack()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                listViewModel.uiState.collect { listUiState ->
                    when (listUiState) {
                        is BaseViewModel.UIState.Success -> {
                            binding.loadingIndicatorList.visibility = View.GONE

                            if (listUiState.value.searchResults != null) {
                                renderResults(listUiState.value.searchResults!!)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "No results! Try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is BaseViewModel.UIState.Error -> {
                            binding.loadingIndicatorList.visibility = View.GONE

                            Toast.makeText(
                                requireContext(),
                                "An error occurred :(",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is BaseViewModel.UIState.Loading -> {
                            binding.loadingIndicatorList.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun renderResults(placeDetails: ArrayList<Restaurant>) {
        searchResults.clear()
        searchResults.addAll(placeDetails)
        binding.list.adapter?.notifyDataSetChanged()
    }
}