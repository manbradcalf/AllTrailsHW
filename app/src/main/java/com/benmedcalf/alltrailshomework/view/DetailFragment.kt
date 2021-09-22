package com.benmedcalf.alltrailshomework.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.benmedcalf.alltrailshomework.BuildConfig
import com.benmedcalf.alltrailshomework.R
import com.benmedcalf.alltrailshomework.databinding.DetailFragmentBinding
import com.benmedcalf.alltrailshomework.model.Restaurant
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailFragment : Fragment(R.layout.detail_fragment) {
    private lateinit var navController: NavController
    private var _binding: DetailFragmentBinding? = null
    private val binding get() = _binding!!
    private val args: DetailFragmentArgs by navArgs()
    lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DetailFragmentBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        setView(args.restaurant)
        return view
    }

    private fun setView(restaurant: Restaurant) {
        binding.detailTitle.text = restaurant.name
        binding.detailPriceLevel.text = Restaurant.formatPrice(restaurant.priceLevel)
        binding.detailRating.rating = restaurant.rating.toFloat()
        val ratingsCountString = "(${restaurant.userRatingsTotal})"
        binding.detailRatingCount.text = ratingsCountString
        val photo = args.restaurant.photoReference
        val photoUrl =
            "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photo_reference=${photo}&key=${BuildConfig.apikey}"
        Glide.with(this)
            .load(photoUrl)
            .centerCrop()
            .placeholder(R.drawable.ic_restaurant)
            .into(binding.detailImage)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.detail_map) as SupportMapFragment?
        navController = findNavController()
        mapFragment?.getMapAsync {
            googleMap = it
            val restaurantLocation = LatLng(
                args.restaurant.geometry.location.lat,
                args.restaurant.geometry.location.lng
            )
            val marker = MarkerOptions().position(restaurantLocation)
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(restaurantLocation, 14.0f))
            it.addMarker(marker)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}