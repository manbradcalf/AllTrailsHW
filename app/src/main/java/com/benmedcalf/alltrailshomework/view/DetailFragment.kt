package com.benmedcalf.alltrailshomework.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.benmedcalf.alltrailshomework.databinding.DetailFragmentBinding
import com.benmedcalf.alltrailshomework.viewmodel.DetailsViewModel

class DetailFragment : BaseFragment() {
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

    override fun setObservers() {
        viewModel.details.observe((viewLifecycleOwner), { placeDetails ->
            _binding?.textview?.text = placeDetails.result.name
            _binding?.textview2?.text = placeDetails.result.rating.toString()
            _binding?.textview3?.text = placeDetails.result.geometry.location.toString()
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(DetailsViewModel::class.java)
        setObservers()
        viewModel.loadRestaurantData(args.placeId)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

