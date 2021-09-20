package com.benmedcalf.alltrailshomework.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.benmedcalf.alltrailshomework.R
import com.benmedcalf.alltrailshomework.databinding.DetailFragmentBinding
import com.benmedcalf.alltrailshomework.model.remote.placeDetails.PlaceDetailsResponse
import com.benmedcalf.alltrailshomework.viewmodel.DetailsViewModel

class DetailFragment : Fragment(R.layout.detail_fragment) {
    private lateinit var viewModel: DetailsViewModel
    private lateinit var navController: NavController
    private var _binding: DetailFragmentBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val args: DetailFragmentArgs by navArgs()

    //  TODO: need a local model of Place
    private var place: PlaceDetailsResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DetailFragmentBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    // TODO(Three things: see below)
    // Move this to Flow and
    // Mutate PlaceDetails -> Restaurant
    // Add favorite functionality
    private fun setObservers() {
        viewModel.details.observe((viewLifecycleOwner), { placeDetails ->
            placeDetails?.result?.let { restaurant ->
                val rating = restaurant.rating.toFloat()
                val userRatings = "(${restaurant.userRatingsTotal})"
                val name = restaurant.name

                binding.detailTitle.text = name
                binding.detailRating.rating = rating
                binding.detailRatingCount.text = userRatings
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(DetailsViewModel::class.java)
        navController = findNavController()
        setObservers()
        viewModel.loadRestaurantData(args.placeId)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}