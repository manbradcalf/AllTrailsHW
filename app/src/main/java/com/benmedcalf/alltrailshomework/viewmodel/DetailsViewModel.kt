package com.benmedcalf.alltrailshomework.viewmodel

import androidx.lifecycle.MutableLiveData
import com.benmedcalf.alltrailshomework.model.PlacesRepository
import com.benmedcalf.alltrailshomework.model.RepoSearchResults
import com.benmedcalf.alltrailshomework.model.remote.GooglePlacesService
import com.benmedcalf.alltrailshomework.model.remote.placeDetails.PlaceDetailsResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel
@Inject constructor(private val repository: PlacesRepository) : BaseViewModel<RepoSearchResults>() {
    val details: MutableLiveData<PlaceDetailsResponse> by lazy {
        MutableLiveData<PlaceDetailsResponse>()
    }
    val name: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun loadRestaurantData(placeId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // make api call in the IO Coroutine Scope
            val request =
                GooglePlacesService.instance.getPlaceDetails("photos,name,rating,geometry", placeId)
            withContext(Dispatchers.Main) {
                if (request.isSuccessful) {
                    request.body()?.let {
                        details.postValue(it)
                    }
                }
            }
        }
    }
}