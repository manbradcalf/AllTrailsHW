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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}