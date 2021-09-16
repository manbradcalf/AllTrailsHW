package com.benmedcalf.alltrailshomework.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.benmedcalf.alltrailshomework.databinding.DetailFragmentBinding
import com.benmedcalf.alltrailshomework.model.remote.placeDetails.PlaceDetailsResponse
import com.benmedcalf.alltrailshomework.viewmodel.DetailsViewModel

class DetailFragment : Fragment() {
    private lateinit var viewModel: DetailsViewModel
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(DetailsViewModel::class.java)
        viewModel.loadRestaurantData(args.placeId)
        viewModel.details.observe((viewLifecycleOwner), {
            _binding?.textview?.text = it.result.name
            _binding?.textview2?.text = it.result.rating.toString()
            _binding?.textview3?.text = it.result.geometry.location.toString()
        })
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

