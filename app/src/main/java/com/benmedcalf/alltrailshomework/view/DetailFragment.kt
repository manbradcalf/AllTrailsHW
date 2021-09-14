package com.benmedcalf.alltrailshomework.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.benmedcalf.alltrailshomework.R
import com.benmedcalf.alltrailshomework.model.remote.PlaceDetailsResponse
import com.benmedcalf.alltrailshomework.viewmodel.RestaurantDetailViewModel

class DetailFragment : Fragment() {
    private lateinit var viewModel: RestaurantDetailViewModel
    // TODO: Should this be lateinit / not nullable?
    private var placeId: String? = null
    private var placeDetails: PlaceDetailsResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.restaurant_detail_fragment, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(RestaurantDetailViewModel::class.java)
        // TODO: Uh, should I have placeId here on initial instantiation?
        savedInstanceState?.let {
            placeId = it.getString(ARG_PLACEID)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // observe the response
        viewModel.restaurantDetailsResponse.observe((viewLifecycleOwner), {
            placeDetails = it
        })
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        const val ARG_PLACEID = "place-id"

        @JvmStatic
        fun newInstance(placeId: String) =
            ListResultsFragment().apply {
                // Set fragment args
                arguments = Bundle().apply {
                    putString(ARG_PLACEID, placeId)
                }
            }
    }
}