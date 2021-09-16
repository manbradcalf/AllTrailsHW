package com.benmedcalf.alltrailshomework.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.benmedcalf.alltrailshomework.model.remote.GooglePlacesService
import com.benmedcalf.alltrailshomework.model.remote.placeDetails.PlaceDetailsResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// TODO: Implement the ViewModel
class SearchViewModel : ViewModel() {
    val searchViewModel: MutableLiveData<PlaceDetailsResponse> by lazy {
        MutableLiveData<PlaceDetailsResponse>()
    }

    fun loadSearchResults(radius: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val request = GooglePlacesService.instance.searchPlaces(radius)
            withContext(Dispatchers.Main) {
                if (request.isSuccessful) {

                }
            }
        }
    }
}