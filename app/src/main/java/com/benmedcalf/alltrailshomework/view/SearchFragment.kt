package com.benmedcalf.alltrailshomework.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.benmedcalf.alltrailshomework.R
import com.benmedcalf.alltrailshomework.databinding.FragmentSearchBinding
import com.benmedcalf.alltrailshomework.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(tag, "we did it")
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.filterbtn.setOnClickListener {
            // TODO("show filtering options")
            viewLifecycleOwner.lifecycleScope.launch {
                searchViewModel.filterSearchResults()
            }
        }

        // Set Up Search Functionality
        binding.searchBox.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewLifecycleOwner.lifecycleScope.launch {
                    searchViewModel.updateSearchResults(query ?: "")
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })

    }
}