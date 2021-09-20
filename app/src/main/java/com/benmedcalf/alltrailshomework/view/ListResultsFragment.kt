package com.benmedcalf.alltrailshomework.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenCreated
import androidx.recyclerview.widget.LinearLayoutManager
import com.benmedcalf.alltrailshomework.R
import com.benmedcalf.alltrailshomework.databinding.FragmentItemListBinding
import com.benmedcalf.alltrailshomework.model.remote.common.Result
import com.benmedcalf.alltrailshomework.viewmodel.ListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListResultsFragment : Fragment(R.layout.fragment_item_list) {
    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!
    private val listViewModel: ListViewModel by viewModels()
    private var searchResults = arrayListOf<Result>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentItemListBinding.inflate(layoutInflater, container, false)
        val view = binding.root

        with(view) {
            layoutManager = LinearLayoutManager(context)
            adapter = PlacesRecyclerViewAdapter(searchResults)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // In the lifecycle coroutine scope
        viewLifecycleOwner.lifecycleScope.launch {
            // When we create fragment
            viewLifecycleOwner.lifecycle.whenCreated {
                // get and subscribe to the UI state
                listViewModel.uiState.collect { listUiState ->
                    renderResults(listUiState.placesRepositoryResult?.value!!.results)
                }
            }
        }
    }

    private fun renderResults(results: List<Result>) {
        Toast.makeText(
            requireContext(),
            "first result is ${results[0].name}",
            Toast.LENGTH_SHORT
        ).show()

        searchResults.addAll(results)
        binding.list.adapter?.notifyDataSetChanged()

    }
}