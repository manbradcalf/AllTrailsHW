package com.benmedcalf.alltrailshomework.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.benmedcalf.alltrailshomework.model.remote.GooglePlacesService
import com.benmedcalf.alltrailshomework.model.remote.PlaceDetailsResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


// TODO: Implement the ViewModel
class RestaurantDetailViewModel : ViewModel() {
    val restaurantDetailsResponse: MutableLiveData<PlaceDetailsResponse> by lazy {
        MutableLiveData<PlaceDetailsResponse>()
    }

    fun loadRestaurantData(placeId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // make api call in the IO Coroutine Scope
            val request = GooglePlacesService.instance.getPlaceDetails(placeId)
            withContext(Dispatchers.Main) {
                if (request.isSuccessful) {
                    request.body()?.let {
                        restaurantDetailsResponse.postValue(it)
                    }
                }
            }
        }
    }
}