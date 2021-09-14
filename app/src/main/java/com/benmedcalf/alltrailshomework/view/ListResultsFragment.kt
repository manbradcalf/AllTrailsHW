package com.benmedcalf.alltrailshomework.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.benmedcalf.alltrailshomework.R
import com.benmedcalf.alltrailshomework.view.placeholder.PlaceholderContent

/**
 * A fragment representing a list of Items.
 */
class ListResultsFragment : Fragment() {

    // Nullable in case user goes to list with no results. Should it be this way?
    private var placesIds: IntArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get the ids of the places (restaurants) we wish to load
        arguments?.let {
            placesIds = it.getIntArray(ARG_PLACES_IDS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = LinearLayoutManager(context)
                adapter = PlacesRecyclerViewAdapter(PlaceholderContent.ITEMS)
            }
        }
        return view
    }

    companion object {
        const val ARG_PLACES_IDS = "places-ids"

        @JvmStatic
        fun newInstance(placesIds: IntArray) =
            ListResultsFragment().apply {
                // Set fragment args
                arguments = Bundle().apply {
                    putIntArray(ARG_PLACES_IDS, placesIds)
                }
            }
    }
}