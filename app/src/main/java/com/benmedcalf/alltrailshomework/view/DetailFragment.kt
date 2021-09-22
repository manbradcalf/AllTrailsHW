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
import com.benmedcalf.alltrailshomework.model.Restaurant
import com.benmedcalf.alltrailshomework.viewmodel.DetailsViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.detail_fragment) {
    private lateinit var viewModel: DetailsViewModel
    private lateinit var navController: NavController
    private var _binding: DetailFragmentBinding? = null
    private val binding get() = _binding!!
    private val args: DetailFragmentArgs by navArgs()

    lateinit var restaurant: Restaurant

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DetailFragmentBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    // Move this to Flow and
    private fun setObservers() {
        viewModel.details.observe((viewLifecycleOwner), { placeDetails ->
            placeDetails?.result?.let { restaurant ->
                val rating = restaurant.rating.toFloat()
                val userRatings = "(${restaurant.userRatingsTotal})"
                val name = restaurant.name

                placeDetails.result.photos?.let { photos ->
                    Glide.with(requireContext()).load(photos[0].photoReference)
                        .into(binding.detailImage)
                }

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