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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.detail_fragment) {
    private lateinit var viewModel: DetailsViewModel
    private lateinit var navController: NavController
    private var _binding: DetailFragmentBinding? = null
    private val binding get() = _binding!!
    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DetailFragmentBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    // Move this to Flow and
    private fun setView(restaurant: Restaurant) {
        binding.detailTitle.text = restaurant.name
        binding.detailRating.rating = restaurant.rating.toFloat()
        val ratingsCountString = "(${restaurant.userRatingsTotal})"
        binding.detailRatingCount.text = ratingsCountString

        if (restaurant.isFavorite) {
            binding.detailHeart.setBackgroundResource(R.drawable.ic_favorited)
        } else {
            binding.detailHeart.setBackgroundResource(R.drawable.ic_unfavorited)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(DetailsViewModel::class.java)
        navController = findNavController()

        setView(args.restaurant)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}